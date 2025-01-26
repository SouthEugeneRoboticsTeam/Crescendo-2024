package org.sert2521.crescendo2024.subsystems

import com.ctre.phoenix6.hardware.CANcoder
import com.revrobotics.spark.SparkBase
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import com.studica.frc.AHRS
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.*
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.MotorSafety
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.crescendo2024.SwerveConstants
import org.sert2521.crescendo2024.SwerveModuleData
import org.sert2521.crescendo2024.commands.JoystickDrive
import kotlin.math.*


class SwerveModule(
    private val driveMotor: SparkMax,
    private val angleMotor: SparkMax,
    private val angleEncoder: CANcoder,
    private val angleOffset: Double,
    var state: SwerveModuleState,
) : MotorSafety() {

    var position: SwerveModulePosition
    private var goal = state
    private var reference = 0.0
    val inverted = false

    init{

        val driveConfig = SparkMaxConfig()
        val angleConfig = SparkMaxConfig()

        // Drive Motors
        driveConfig.inverted(SwerveConstants.DRIVE_MOTOR_INVERTED)
        driveConfig.idleMode(SparkBaseConfig.IdleMode.kBrake)
        driveConfig.smartCurrentLimit(SwerveConstants.DRIVE_CURRENT_LIMIT)

        driveConfig.closedLoop.p(SwerveConstants.DRIVE_P)
        driveConfig.closedLoop.i(SwerveConstants.DRIVE_I)
        driveConfig.closedLoop.d(SwerveConstants.DRIVE_D)
        driveConfig.closedLoop.velocityFF(SwerveConstants.DRIVE_V)

        driveConfig.encoder.positionConversionFactor(SwerveConstants.DRIVE_ENCODER_MULTIPLY_POSITION)
        driveConfig.encoder.velocityConversionFactor(SwerveConstants.DRIVE_ENCODER_MULTIPLY_VELOCITY)

        // Angle Motors
        angleConfig.inverted(SwerveConstants.ANGLE_MOTOR_INVERTED)
        angleConfig.idleMode(SparkBaseConfig.IdleMode.kBrake)
        angleConfig.smartCurrentLimit(SwerveConstants.ANGLE_CURRENT_LIMIT)

        angleConfig.closedLoop.p(SwerveConstants.ANGLE_P)
        angleConfig.closedLoop.i(SwerveConstants.ANGLE_I)
        angleConfig.closedLoop.d(SwerveConstants.ANGLE_D)

        angleConfig.closedLoop.positionWrappingEnabled(true)
        angleConfig.closedLoop.positionWrappingMinInput(-PI)
        angleConfig.closedLoop.positionWrappingMaxInput(PI)

        // Angle Encoders
        angleConfig.encoder.positionConversionFactor(SwerveConstants.ANGLE_ENCODER_MULTIPLY)
        angleConfig.encoder.velocityConversionFactor(SwerveConstants.ANGLE_ENCODER_MULTIPLY / 60)

        position = SwerveModulePosition(driveMotor.encoder.position, getAngle())
    }

    fun getAngle(): Rotation2d {
        if(inverted) {
            angleMotor.encoder.setPosition(angleEncoder.absolutePosition.valueAsDouble * SwerveConstants.ANGLE_ENCODER_MULTIPLY - angleOffset)
        }else{
            angleMotor.encoder.setPosition((-(angleEncoder.absolutePosition.valueAsDouble * SwerveConstants.ANGLE_ENCODER_MULTIPLY - angleOffset)))
        }

        return Rotation2d((angleMotor.encoder.position+ PI).mod(2*PI)-PI)
    }

    // Run periodically
    fun updateState() {
        val angle = getAngle()
        state = SwerveModuleState(driveMotor.encoder.velocity, angle)
        position = SwerveModulePosition(driveMotor.encoder.position, angle)
    }

    fun set(wanted: SwerveModuleState) {
        wanted.optimize(getAngle())
        val driveError = wanted.speedMetersPerSecond - driveMotor.encoder.velocity

        goal = SwerveModuleState(wanted.speedMetersPerSecond, Rotation2d(wanted.angle.radians))
        reference = driveError.pow(2) * sign(driveError) + driveMotor.encoder.velocity
        angleMotor.closedLoopController.setReference(wanted.angle.radians, SparkBase.ControlType.kPosition)

    }

    fun getModuleGoal():SwerveModuleState { return SwerveModuleState(goal.speedMetersPerSecond, Rotation2d(goal.angle.radians)) }

    fun setMotorMode(coast: Boolean, driveConfig:SparkMaxConfig, angleConfig:SparkMaxConfig) {

        if(coast) {
            driveConfig.idleMode(SparkBaseConfig.IdleMode.kCoast)
            angleConfig.idleMode(SparkBaseConfig.IdleMode.kCoast)
        } else {
            driveConfig.idleMode(SparkBaseConfig.IdleMode.kBrake)
            angleConfig.idleMode(SparkBaseConfig.IdleMode.kBrake)
        }


    }

    fun getModuleReference(): Double { return reference }

    fun getAmps() :Pair<Double, Double> { return Pair(driveMotor.outputCurrent, angleMotor.outputCurrent) }

    fun setCurrentLimit(amps:Int, driveConfig: SparkMaxConfig) { driveConfig.smartCurrentLimit(amps) }

    override fun stopMotor() {
        driveMotor.stopMotor()
        angleMotor.stopMotor()
    }

    fun getEncoderHealth(): Double { return angleEncoder.magnetHealth.valueAsDouble }

    override fun getDescription(): String { return "Swerve"
    }
}

object Drivetrain : SubsystemBase() {

    private val imu = AHRS(AHRS.NavXComType.kMXP_SPI)

    private val kinematics: SwerveDriveKinematics
    private var modules: Array<SwerveModule>
    private var odometry: SwerveDriveOdometry
    private val poseEstimator: SwerveDrivePoseEstimator

    private var pose = Pose2d()

    private var prevPose = Pose2d()
    private var prevTime = Timer.getFPGATimestamp()

    var deltaPose = Pose2d()

    init {

        val modulePositions = mutableListOf<Translation2d>()
        val moduleList = mutableListOf<SwerveModule>()

        for (moduleData in SwerveConstants.swerveModuleData) {
            val driveMotor = SparkMax(moduleData.driveMotorID, SparkLowLevel.MotorType.kBrushless)
            val angleMotor = SparkMax(moduleData.angleMotorID, SparkLowLevel.MotorType.kBrushless)

            modulePositions.add(moduleData.position)
            val module = createModule(driveMotor, angleMotor, moduleData)
            module.isSafetyEnabled = true
            moduleList.add(module)
        }

        modules = moduleList.toTypedArray()

        val positions = mutableListOf<SwerveModulePosition>()

        for (module in modules) {
            module.updateState()
            positions.add(module.position)
        }

        val positionsArray = positions.toTypedArray()

        kinematics = SwerveDriveKinematics(*modulePositions.toTypedArray())
        odometry = SwerveDriveOdometry(kinematics, getYawAsRotation2d(), positionsArray, Pose2d())
        poseEstimator = SwerveDrivePoseEstimator(kinematics, getYawAsRotation2d(), positionsArray, Pose2d())

        Drivetrain.defaultCommand = JoystickDrive(true)

    }


    // "Fix this nonsense" -Whoever made the original code
    private fun createModule(driveMotor: SparkMax, angleMotor:SparkMax, moduleData: SwerveModuleData): SwerveModule {

        return SwerveModule(
            driveMotor,
            angleMotor,
            CANcoder(moduleData.angleEncoderID),
            moduleData.angleOffset,
            SwerveModuleState()
        )

    }

    override fun periodic() {

        val positions = mutableListOf<SwerveModulePosition>()

        for (module in modules) {
            module.updateState()
            positions.add(module.position)
        }

        val positionsArray = positions.toTypedArray()

        pose = odometry.update(getYawAsRotation2d(), positionsArray)

        val currTime = Timer.getFPGATimestamp()
        val deltaTime = currTime - prevTime

        poseEstimator.update(getYawAsRotation2d(), positionsArray)

        deltaPose = Pose2d((pose.y - prevPose.y) / deltaTime, (pose.x - prevPose.x) / deltaTime, -(pose.rotation - prevPose.rotation) / deltaTime)

        prevPose = pose
        prevTime = currTime

    }

    fun setNewPose(newPose:Pose2d) {

        pose = Pose2d(newPose.y, newPose.x, newPose.rotation)

        val positions = mutableListOf<SwerveModulePosition>()

        for (module in modules) {
            module.updateState()
            positions.add(module.position)
        }

        val positionsArray = positions.toTypedArray()

        odometry.resetPosition(getYawAsRotation2d(), positionsArray, pose)

    }

    private fun feed() {

        for (module in modules) {
            module.feed()
        }
    }

    fun drive(chassisSpeeds: ChassisSpeeds) {

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

    fun getTilt(): Double { return atan(sqrt(tan(Units.degreesToRadians(imu.pitch.toDouble())).pow(2) + tan(Units.degreesToRadians(imu.roll.toDouble())).pow(2))) }

    fun getRoll(): Double { return Units.degreesToRadians(imu.roll.toDouble()) }

    fun getAccelerationSquared():Double { return (imu.worldLinearAccelY.pow(2) + imu.worldLinearAccelX.pow(2)).toDouble() }

    fun getGoals(): Array<SwerveModuleState> { return arrayOf(modules[0].getModuleGoal(), modules[1].getModuleGoal(), modules[2].getModuleGoal(), modules[3].getModuleGoal()) }

    fun getReferences(): Array<Double> { return arrayOf(modules[0].getModuleReference(), modules[1].getModuleReference(), modules[2].getModuleReference(), modules[3].getModuleReference()) }

    fun getAmps(): Array<Pair<Double, Double>>{ return arrayOf(modules[0].getAmps(), modules[1].getAmps(), modules[2].getAmps(), modules[3].getAmps()) }

    fun getDraw(): Double{ return modules[0].getAmps().first+modules[1].getAmps().first+modules[2].getAmps().first+modules[3].getAmps().first }

    fun getStates(): Array<SwerveModuleState>{ return arrayOf(modules[0].state, modules[1].state, modules[2].state, modules[3].state) }

    fun getHealth(module: Int): Double { return modules[module].getEncoderHealth() }

    fun getYaw(): Double { return -imu.yaw.toDouble() }

    fun getYawAsRotation2d(): Rotation2d { return Rotation2d.fromDegrees(-imu.angle) }

    fun getRelativeSpeeds(): ChassisSpeeds { return kinematics.toChassisSpeeds(*arrayOf(modules[0].state, modules[1].state, modules[2].state, modules[3].state)) } // Yes it says that the asterisk is wrong, but it is correct.

    fun getAbsoluteSpeeds(): ChassisSpeeds { return ChassisSpeeds.fromRobotRelativeSpeeds(getRelativeSpeeds(), getPose().rotation) }

    // "Fix this nonsense" -Whoever made the original code
    fun getPose(): Pose2d {
        return Pose2d(pose.y, pose.x, -pose.rotation)
    }

    fun setMode(coast: Boolean, driveConfig: SparkMaxConfig, angleConfig: SparkMaxConfig) {

        for (module in modules) {
            module.setMotorMode(coast, driveConfig, angleConfig)
        }
    }

    fun setCurrentLimit(amps: Int, driveConfig: SparkMaxConfig) {

        for (module in modules) {
            module.setCurrentLimit(amps, driveConfig)
        }
    }

    fun stop() {

        for (module in modules) {
            module.stopMotor()
        }

        feed()

    }
}