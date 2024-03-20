package org.sert2521.crescendo2024.subsystems

import com.ctre.phoenix6.hardware.CANcoder
import com.kauailabs.navx.frc.AHRS
import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkLowLevel
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator
import edu.wpi.first.math.geometry.*
import edu.wpi.first.math.kinematics.*
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.MotorSafety
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
//import org.photonvision.PhotonCamera
//import org.photonvision.PhotonPoseEstimator
import org.sert2521.crescendo2024.*
import org.sert2521.crescendo2024.commands.JoystickDrive
import kotlin.math.*

class SwerveModule(private val powerMotor: CANSparkMax,
                   private val angleMotor: CANSparkMax,
                   private val angleEncoder: CANcoder,
                   private val angleOffset: Double,
                   private val inverted: Boolean,
                   var state: SwerveModuleState,
                   brakeMode: Boolean) : MotorSafety() {


    var position: SwerveModulePosition

    private var goal = state

    private var reference = 0.0

    init {
        powerMotor.restoreFactoryDefaults()
        angleMotor.restoreFactoryDefaults()

        powerMotor.idleMode = CANSparkBase.IdleMode.kBrake
        angleMotor.idleMode = CANSparkBase.IdleMode.kCoast

        powerMotor.pidController.p = SwerveConstants.POWER_P
        powerMotor.pidController.i = SwerveConstants.POWER_I
        powerMotor.pidController.d = SwerveConstants.POWER_D
        powerMotor.pidController.ff = SwerveConstants.POWER_V

        angleMotor.pidController.p = SwerveConstants.ANGLE_P
        angleMotor.pidController.i = SwerveConstants.ANGLE_I
        angleMotor.pidController.d = SwerveConstants.ANGLE_D

        angleMotor.encoder.positionConversionFactor = SwerveConstants.ANGLE_MOTOR_ENCODER_MULTIPLY
        angleMotor.encoder.velocityConversionFactor = SwerveConstants.ANGLE_MOTOR_ENCODER_MULTIPLY / 60.0


        angleMotor.pidController.positionPIDWrappingEnabled = true
        angleMotor.pidController.positionPIDWrappingMinInput = -PI
        angleMotor.pidController.positionPIDWrappingMaxInput = PI

        powerMotor.inverted = inverted
        angleMotor.inverted = inverted

        powerMotor.encoder.positionConversionFactor = SwerveConstants.POWER_ENCODER_MULTIPLY_POSITION
        powerMotor.encoder.velocityConversionFactor = SwerveConstants.POWER_ENCODER_MULTIPLY_VELOCITY



        powerMotor.setSmartCurrentLimit(40)
        angleMotor.setSmartCurrentLimit(30)

        position = SwerveModulePosition(powerMotor.encoder.position, getAngle())

        setMotorMode(!brakeMode)
    }

    fun getAngle(): Rotation2d {
        if (inverted){
            angleMotor.encoder.setPosition(angleEncoder.absolutePosition.valueAsDouble * SwerveConstants.ANGLE_ENCODER_MULTIPLY - angleOffset)
        } else {
            angleMotor.encoder.setPosition(-(angleEncoder.absolutePosition.valueAsDouble * SwerveConstants.ANGLE_ENCODER_MULTIPLY - angleOffset))
        }

        return Rotation2d((angleMotor.encoder.position+PI).mod(2*PI)-PI)
    }

    // Should be called in periodic
    fun updateState() {
        val angle = getAngle()
        state = SwerveModuleState(powerMotor.encoder.velocity, angle)
        position = SwerveModulePosition(powerMotor.encoder.position, angle)
    }

    fun set(wanted: SwerveModuleState) {
        // Using state because it should be updated and getVelocity and getAngle (probably) spend time over CAN
        val optimized = SwerveModuleState.optimize(wanted, state.angle)
        val powerError = optimized.speedMetersPerSecond-powerMotor.encoder.velocity
        /*
        val feedforward = powerFeedforward.calculate(optimized.speedMetersPerSecond)
        val pid = if (inverted) {

            powerPID.calculate(-state.speedMetersPerSecond, optimized.speedMetersPerSecond)
        } else {
            powerPID.calculate(state.speedMetersPerSecond, optimized.speedMetersPerSecond)
        }

        // Why isn't motor.inverted working if it isn't
        if (!inverted) {
            powerMotor.set((feedforward + pid) / 12.0)
        } else {
            powerMotor.set(-(feedforward + pid) / 12.0)
        }

         */
        goal= SwerveModuleState(optimized.speedMetersPerSecond, Rotation2d(optimized.angle.radians))

        reference=powerError.pow(2)*sign(powerError)+powerMotor.encoder.velocity

        powerMotor.pidController.setReference(optimized.speedMetersPerSecond, CANSparkBase.ControlType.kVelocity)

        //maybe -angleOffset
        angleMotor.pidController.setReference(optimized.angle.radians, CANSparkBase.ControlType.kPosition)
    }

    fun getModuleGoal():SwerveModuleState{
        return SwerveModuleState(goal.speedMetersPerSecond, Rotation2d(goal.angle.radians))
    }

    fun setMotorMode(coast: Boolean) {
        if (coast) {
            powerMotor.idleMode = CANSparkBase.IdleMode.kCoast
            angleMotor.idleMode = CANSparkBase.IdleMode.kCoast
        } else {
            powerMotor.idleMode = CANSparkBase.IdleMode.kBrake
            angleMotor.idleMode = CANSparkBase.IdleMode.kBrake
        }
    }

    fun getModuleReference():Double{
        return reference
    }

    fun getAmps():Pair<Double, Double>{
        return Pair(powerMotor.outputCurrent, angleMotor.outputCurrent)
    }

    fun setCurrentLimit(amps:Int){
        powerMotor.setSmartCurrentLimit(amps)
    }

    override fun stopMotor() {
        powerMotor.stopMotor()
        angleMotor.stopMotor()
    }

    override fun getDescription(): String {
        return "Swerve Module"
    }
}

object Drivetrain : SubsystemBase() {
    private val imu = AHRS()

    //private val cams: Array<PhotonCamera>
    //private val photonPoseEstimators: Array<PhotonPoseEstimator>

    private val kinematics: SwerveDriveKinematics
    private var modules: Array<SwerveModule>
    private val odometry: SwerveDriveOdometry
    private val poseEstimator: SwerveDrivePoseEstimator

    private var pose = Pose2d()
    private var visionPose = Pose2d()

    private var prevPose = Pose2d()
    private var prevTime = Timer.getFPGATimestamp()


    var deltaPose = Pose2d()
        private set

    // False is broken
    var doesOptimize = ConfigConstants.DRIVE_OPTIMIZED
        private set

    init {

        val modulePositions = mutableListOf<Translation2d>()
        val modulesList = mutableListOf<SwerveModule>()

        // Maybe the module should create the motors
        for (moduleData in SwerveConstants.swerveModuleData) {
            val powerMotor = CANSparkMax(moduleData.powerMotorID, CANSparkLowLevel.MotorType.kBrushless)
            val angleMotor = CANSparkMax(moduleData.angleMotorID, CANSparkLowLevel.MotorType.kBrushless)

            modulePositions.add(moduleData.position)
            val module = createModule(powerMotor, angleMotor, moduleData)
            module.isSafetyEnabled = true
            modulesList.add(module)
        }

        modules = modulesList.toTypedArray()

        val positions = mutableListOf<SwerveModulePosition>()

        for (module in modules) {
            module.updateState()
            positions.add(module.position)
        }

        val positionsArray = positions.toTypedArray()

        kinematics = SwerveDriveKinematics(*modulePositions.toTypedArray())
        odometry = SwerveDriveOdometry(kinematics, -imu.rotation2d, positionsArray, Pose2d())
        poseEstimator = SwerveDrivePoseEstimator(kinematics, -imu.rotation2d, positionsArray, Pose2d())
        /*
        val camsList = mutableListOf<PhotonCamera>()
        val photonPoseEstimatorsList = mutableListOf<PhotonPoseEstimator>()
        for (camData in ElectronicIDs.camData) {
            val cam = PhotonCamera(camData.first)
            camsList.add(cam)
            // Field gets updated before run so it can be null
            photonPoseEstimatorsList.add(PhotonPoseEstimator(PhysicalConstants.field, PhotonPoseEstimator.PoseStrategy.AVERAGE_BEST_TARGETS, cam, camData.second))
        }

        cams = camsList.toTypedArray()
        photonPoseEstimators = photonPoseEstimatorsList.toTypedArray()
         */
        Drivetrain.defaultCommand = JoystickDrive(true)
    }

    // Fix this nonsense
    fun getPose(): Pose2d {
        return Pose2d(pose.y, pose.x, -pose.rotation)
    }

    fun getVisionPose(): Pose2d {
        return Pose2d(visionPose.y, visionPose.x, -visionPose.rotation)
    }

    // Fix this nonsense

    private fun createModule(powerMotor: CANSparkMax, angleMotor: CANSparkMax, moduleData: SwerveModuleData): SwerveModule {
        return SwerveModule(powerMotor,
            angleMotor,
            CANcoder(moduleData.angleEncoderID),
            moduleData.angleOffset,
            moduleData.inverted,
            SwerveModuleState(),
            true
        )
    }

    override fun periodic() {
        val positions = mutableListOf<SwerveModulePosition>()

        for (module in modules) {
            module.updateState()
            positions.add(module.position)
        }
        //println(listOf(modules[0].getAngle().radians, modules[1].getAngle().radians, modules[2].getAngle().radians, modules[3].getAngle().radians))

        val positionsArray = positions.toTypedArray()

        pose = odometry.update(-imu.rotation2d, positionsArray)
        visionPose = poseEstimator.update(-imu.rotation2d, positionsArray)
        /*
        for (photonPoseEstimator in photonPoseEstimators) {
            val poseOutput = photonPoseEstimator.update()
            if (poseOutput.isPresent) {
                val currVisionPoseData = poseOutput.get()
                val currVisionPose = currVisionPoseData.estimatedPose.toPose2d()

                val color = Input.getColor()
                if (color == DriverStation.Alliance.Blue) {
                    poseEstimator.addVisionMeasurement(Pose2d(currVisionPose.y, currVisionPose.x, -currVisionPose.rotation), currVisionPoseData.timestampSeconds)
                } else if (color == DriverStation.Alliance.Red) {
                    poseEstimator.addVisionMeasurement(Pose2d(PhysicalConstants.FIELD_WIDTH - currVisionPose.y, PhysicalConstants.FIELD_LENGTH - currVisionPose.x, Rotation2d(PI) - currVisionPose.rotation), currVisionPoseData.timestampSeconds)
                }
            }
        }

         */

        val currTime = Timer.getFPGATimestamp()
        val deltaTime = currTime - prevTime

        deltaPose = Pose2d((pose.y - prevPose.y) / deltaTime, (pose.x - prevPose.x) / deltaTime, -(pose.rotation - prevPose.rotation) / deltaTime)

        prevPose = pose
        prevTime = currTime
    }

    fun setNewPose(newPose: Pose2d) {
        pose = Pose2d(newPose.y, newPose.x, -newPose.rotation)

        val positions = mutableListOf<SwerveModulePosition>()

        for (module in modules) {
            module.updateState()
            positions.add(module.position)
        }

        val positionsArray = positions.toTypedArray()

        odometry.resetPosition(-imu.rotation2d, positionsArray, pose)
    }
    fun setNewVisionPose(newPose: Pose2d) {
        visionPose = Pose2d(newPose.y, newPose.x, -newPose.rotation)

        val positions = mutableListOf<SwerveModulePosition>()

        for (module in modules) {
            module.updateState()
            positions.add(module.position)
        }

        val positionsArray = positions.toTypedArray()

        poseEstimator.resetPosition(-imu.rotation2d, positionsArray, visionPose)
    }

    fun setVisionStandardDeviations() {
        poseEstimator.setVisionMeasurementStdDevs(TuningConstants.defaultVisionDeviations)
    }

    fun setVisionAlignDeviations() {
        poseEstimator.setVisionMeasurementStdDevs(TuningConstants.alignVisionDeviations)
    }
    /*
    fun visionSeeingThings(): Boolean {
        val time = Timer.getFPGATimestamp()
        if (camerasConnected()) {
            return cams.any { time - it.latestResult.timestampSeconds < TuningConstants.VISION_TIMEOUT }
        }

        return false
    }

    fun camerasConnected(): Boolean {
        return cams.all { it.isConnected }
    }

     */

    fun getReletiveSpeeds():ChassisSpeeds{
        return kinematics.toChassisSpeeds(modules[0].state, modules[1].state, modules[2].state, modules[3].state)
    }

    fun getAbsoluteSpeeds():ChassisSpeeds{
        return ChassisSpeeds.fromRobotRelativeSpeeds(getReletiveSpeeds(), getPose().rotation)
    }

    fun getAccelSqr(): Double {
        return (imu.worldLinearAccelY.pow(2) + imu.worldLinearAccelX.pow(2)).toDouble()
    }

    private fun feed() {
        for (module in modules) {
            module.feed()
        }
    }

    fun drive(chassisSpeeds: ChassisSpeeds) {
        // Maybe desaturate wheel speeds
        // Fix this ChassisSpeeds nonsense
        val wantedStates = kinematics.toSwerveModuleStates(ChassisSpeeds(chassisSpeeds.vyMetersPerSecond, chassisSpeeds.vxMetersPerSecond, -chassisSpeeds.omegaRadiansPerSecond))

        for (i in wantedStates.indices) {
            modules[i].set(wantedStates[i])
        }

        feed()
    }

    fun getTiltDirection(): Translation2d {
        val unNormalized = Translation2d(atan(Units.degreesToRadians(imu.roll.toDouble())), atan(Units.degreesToRadians(imu.pitch.toDouble())))
        val norm = unNormalized.norm

        if (norm == 0.0) {
            return unNormalized
        }

        return unNormalized / norm
    }

    fun getTilt(): Double {
        return atan(sqrt(tan(Units.degreesToRadians(imu.pitch.toDouble())).pow(2) + tan(Units.degreesToRadians(imu.roll.toDouble())).pow(2)))
    }

    fun getRoll(): Double{
        return Units.degreesToRadians(imu.roll.toDouble())
    }

    fun getGoals():Array<SwerveModuleState>{
        return arrayOf(modules[0].getModuleGoal(), modules[1].getModuleGoal(), modules[2].getModuleGoal(), modules[3].getModuleGoal())
    }

    fun getReferences():Array<Double>{
        return arrayOf(modules[0].getModuleReference(), modules[1].getModuleReference(), modules[2].getModuleReference(), modules[3].getModuleReference())
    }

    fun getAmps():Array<Pair<Double, Double>>{
        return arrayOf(modules[0].getAmps(), modules[1].getAmps(), modules[2].getAmps(), modules[3].getAmps())
    }

    fun getStates():Array<SwerveModuleState>{
        return arrayOf(modules[0].state, modules[1].state, modules[2].state, modules[3].state)
    }

    fun setMode(coast: Boolean) {
        for (module in modules) {
            module.setMotorMode(coast)
        }
    }

    fun enterClimbPos(){
        for (module in modules){
            module.set(SwerveModuleState(0.0, Rotation2d(0.0)))
        }
    }

    fun setCurrentLimit(amps:Int){
        for (module in modules){
            module.setCurrentLimit(amps)
        }
    }

    fun stop() {
        for (module in modules) {
            module.stopMotor()
        }

        feed()
    }
}