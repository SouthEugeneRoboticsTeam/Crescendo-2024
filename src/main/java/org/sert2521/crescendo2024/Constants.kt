package org.sert2521.crescendo2024

import edu.wpi.first.apriltag.AprilTagFieldLayout
import edu.wpi.first.apriltag.AprilTagFields
import edu.wpi.first.math.MatBuilder.fill
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.Nat
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.util.Units
import edu.wpi.first.apriltag.AprilTag
import edu.wpi.first.math.geometry.*
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap
import kotlin.math.PI


//gear ratio means (1/gear ratio) equals positionConversionFactor
/*
 * The Constants file provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This file should not be used for any other purpose.
 * All String, Boolean, and numeric (Int, Long, Float, Double) constants should use
 * `const` definitions. Other constant types should use `val` definitions.
 */
class SwerveModuleData(val position: Translation2d, val powerMotorID: Int, val angleMotorID: Int, val angleEncoderID: Int, val angleOffset: Double, val inverted: Boolean)

object PhysicalConstants{
    const val CLIMBER_MAX = 10.0
    const val CLIMBER_MIN = 0.0

    const val HALF_SIDE_LENGTH = 0.0

    const val WRIST_ENCODER_MULTIPLY = -2*PI
    var WRIST_ENCODER_OFFSET = -0.197

    //ESTIMATES
    const val WRIST_SETPOINT_STOW = -0.2
    const val WRIST_SETPOINT_AMP = 1.53
    const val WRIST_SETPOINT_PODIUM = 0.16
    const val WRIST_SETPOINT_FAR = 0.295
    const val WRIST_SETPOINT_PODIUM_MINUS = 0.1
    const val WRIST_SETPOINT_PODIUM_PLUS = 0.215
    const val WRIST_SETPOINT_PODIUM_PLUS_HALF = 0.235
    var WRIST_SETPOINT_PODIUM_DOUBLE_PLUS = 0.2927
    const val WRIST_SETPOINT_PODIUM_TRIPLE_PLUS = 0.33
    const val WRIST_SETPOINT_SOURCE = 0.869

    val aprilTagField: AprilTagFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField()
    val FIELD_WIDTH = aprilTagField.fieldWidth
    val FIELD_LENGTH = aprilTagField.fieldLength

    val usedTags = listOf(2, 3, 6, 7) // -1 because indexes and shit
    val usedFieldTags = mutableListOf<AprilTag>()
    var usedField: AprilTagFieldLayout

    init {
        for (tag in usedTags){
            usedFieldTags.add(aprilTagField.tags[tag])
        }
        usedField = AprilTagFieldLayout(usedFieldTags, aprilTagField.fieldLength, aprilTagField.fieldWidth)
    }

    val centerPose = Transform3d(Translation3d(Units.inchesToMeters(-10.029), Units.inchesToMeters(6.081), Units.inchesToMeters(15.26)), Rotation3d(0.0, 0.349, PI))

    val speakerTransRed = Translation2d(aprilTagField.tags[3].pose.translation.toTranslation2d().x, aprilTagField.tags[3].pose.translation.toTranslation2d().y+0.2)
    val speakerTransBlue = Translation2d(aprilTagField.tags[7].pose.translation.toTranslation2d().x, aprilTagField.tags[7].pose.translation.toTranslation2d().y+0.2)

    const val FLYWHEEL_GEAR_RATIO = 3.0/2.0

    const val CLIMBER_ENCODER_TO_METERS = 1.0

    const val CLIMBER_BETWEEN_DISTANCE = 1.0
}

object ConfigConstants{
    const val POWER_DEADBAND = 0.05
    const val ROT_DEADBAND = 0.07

    //Drive speed constants
    const val DRIVE_SPEED = 5.3
    const val DRIVE_SECONDARY_SPEED = 2.75
    const val ROT_SPEED = 6.0
    const val ROT_SECONDARY_SPEED = 2.75

    //Acceleration of drivetrain
    const val DRIVE_ACCEL = 35.0
    const val DRIVE_DECCEL = 40.0

    const val DRIVE_OPTIMIZED = true

    const val FLYWHEEL_IDLE_SPEED = 0.0
    const val FLYWHEEL_SHOOT_SPEED = 5000.0
}

object SwerveConstants{
    const val HALF_SIDE_LENGTH = 0.263525
    const val DRIVE_BASE_RADIUS = 0.37268

    const val POWER_S = 0.0
    const val POWER_V = 0.2
    const val POWER_A = 0.0

    const val POWER_P = 0.05
    const val POWER_I = 0.0
    const val POWER_D = 0.0

    const val ANGLE_P = 1.4
    const val ANGLE_I = 0.0
    const val ANGLE_D = 0.0

    val swerveModuleData = listOf(
            SwerveModuleData(Translation2d(HALF_SIDE_LENGTH, -HALF_SIDE_LENGTH), 5, 7, 16, -0.355-1.61, false), //Back Left
            SwerveModuleData(Translation2d(-HALF_SIDE_LENGTH, -HALF_SIDE_LENGTH), 1, 2, 15, -0.138-1.57, false), //Back Right
            SwerveModuleData(Translation2d(HALF_SIDE_LENGTH, HALF_SIDE_LENGTH), 16, 15, 14, 2.41-1.612, false), //Front Left
            SwerveModuleData(Translation2d(-HALF_SIDE_LENGTH, HALF_SIDE_LENGTH), 3, 12, 13, 0.059-1.568, false)) //Front Right

    // Pi * diameter / gear ratio
    const val POWER_ENCODER_MULTIPLY_POSITION = PI * 0.1016 / 5.903
    // Velocity is in rpm so needs / 60
    const val POWER_ENCODER_MULTIPLY_VELOCITY = POWER_ENCODER_MULTIPLY_POSITION / 60.0


    const val ANGLE_ENCODER_MULTIPLY = 2* PI

    const val ANGLE_MOTOR_ENCODER_MULTIPLY = 1/21.4285714 * (2*PI)

    const val MAX_AUTO_SPEED = 4.0

    const val AUTO_POWER_P = 2.0
    const val AUTO_POWER_I = 0.0
    const val AUTO_POWER_D = 0.0

    const val AUTO_ANGLE_P = 2.0
    const val AUTO_ANGLE_I = 0.0
    const val AUTO_ANGLE_D = 0.0

    const val AUTO_REPLANNING_TOTAL_ERROR = 100.0
    const val AUTO_REPLANNING_SPIKE = 100.0

}

object ElectronicIDs{
    const val INTAKE_MOTOR_ID = 10
    const val INTAKE_ALIGNMENT_MOTOR_ID = 13
    const val WRIST_ONE_ID = 17
    const val WRIST_TWO_ID = 4
    const val INDEXER_MOTOR_ID = 9
    const val BEAMBREAK_ID = 3
    const val FLYWHEEL_MOTOR_ONE_ID = 8
    const val FLYWHEEL_MOTOR_TWO_ID = 11
    const val CLIMBER_MOTOR_ONE = 6
    const val CLIMBER_MOTOR_TWO = 14

    //val camData:Pair<String, Translation3d> = listOf(/*Pair("Center", PhysicalConstants.centerPose), Pair("Right2", PhysicalConstants.rightPose), Pair("Left2", PhysicalConstants.frontPose)*/)
}

object RuntimeConstants{
    var motorSpeed = 0.0
    var wristSetPoint = PhysicalConstants.WRIST_SETPOINT_STOW
    var flywheelRevved = false
    var flywheelGoal = 0.0
    var visionAligning = false
    var visionRightStick = 0.0
    var wristVision = PhysicalConstants.WRIST_SETPOINT_STOW
}

object TuningConstants {
    const val VIS_DRIVE_OFFSET_MULT = 0.0
    const val VIS_WRIST_OFFSET_MULT = 0.0
    //Key = Meters from target, value = arm angle
    val wristAngLookup = InterpolatingDoubleTreeMap()

    init{
        wristAngLookup.put(1.04, PhysicalConstants.WRIST_SETPOINT_STOW)
        wristAngLookup.put(1.554, -0.026)
        wristAngLookup.put(1.8, 0.074)
        wristAngLookup.put(2.2, 0.165)
        wristAngLookup.put(2.5, 0.207)
        wristAngLookup.put(2.8, 0.22)
        wristAngLookup.put(3.04, 0.256)
        wristAngLookup.put(3.23, 0.275)
        wristAngLookup.put(3.48, 0.307)
        wristAngLookup.put(4.025, 0.354)
        wristAngLookup.put(4.49, 0.387)
        wristAngLookup.put(4.82, 0.4)
        wristAngLookup.put(5.25, 0.423)
        wristAngLookup.put(5.74, 0.43)
        wristAngLookup.put(6.53, 0.449)
    }

    val defaultVisionDeviations: Matrix<N3, N1> = fill(Nat.N3(), Nat.N1(), 1.0, 1.0, 1.0)
    val alignVisionDeviations: Matrix<N3, N1> = fill(Nat.N3(), Nat.N1(),3.0, 3.0, 2.0)

    const val VISION_TIMEOUT = 0.1

    const val WRIST_P = 30.0
    const val WRIST_I = 0.0
    const val WRIST_D = 0.0

    //ESTIMATIONS
    const val WRIST_S = 0.0
    const val WRIST_G = 0.365
    const val WRIST_V = 0.0
    const val WRIST_A = 0.0

    const val VISION_ALIGN_P = 9.0
    const val VISION_ALIGN_I = 0.0
    const val VISION_ALIGN_D = 0.4

    const val VISION_ALIGN_S = -0.19

    const val VISION_TOLERANCE = 0.01
    const val VISION_WRIST_TOLERANCE = 0.2

    const val WRIST_ANGLE_TOLERANCE = 0.1
    val trapConstraints = TrapezoidProfile.Constraints(5.0, 15.0)

    const val FLYWHEEL_P = 0.001
    const val FLYWHEEL_I = 0.0
    const val FLYWHEEL_D = 0.0

    const val FLYWHEEL_P_COR = 0.001
    const val FLYWHEEL_I_COR = 0.0
    const val FLYWHEEL_D_COR = 0.0

    const val FLYWHEEL_OFFSET = 10.0

    const val FLYWHEEL_KS = 0.0
    const val FLYWHEEL_KV = 0.0015
    const val FLYWHEEL_KA = 0.0

    const val FLYWHEEL_IDLE_SPEED = 0.0

    const val CLIMBER_FILTER = 0.0

    const val CLIMBER_TOLERANCE_ANGLE = 0.0
    const val CLIMBER_RESTING_TOLERANCE = 0.0

    const val CLIMBER_TOLERANCE_ENCODER = 0.0

    const val CLIMBER_STALL_TOLERANCE = 0.5
    const val CLIMBER_STALL_TRY_POWER = 0.1
    const val CLIMBER_STALL_SPEED = 0.1

    const val CLIMB_SPEED = 0.0
}
