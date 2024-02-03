package org.sert2521.crescendo2024

import edu.wpi.first.apriltag.AprilTagFieldLayout
import edu.wpi.first.apriltag.AprilTagFields
import edu.wpi.first.math.MatBuilder.fill
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.Nat
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Transform3d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.util.Units
import kotlin.math.PI

/*
 * The Constants file provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This file should not be used for any other purpose.
 * All String, Boolean, and numeric (Int, Long, Float, Double) constants should use
 * `const` definitions. Other constant types should use `val` definitions.
 */
class SwerveModuleData(val position: Translation2d, val powerMotorID: Int, val angleMotorID: Int, val angleEncoderID: Int, val angleOffset: Double, val inverted: Boolean)

object PhysicalConstants{
    const val HALF_SIDE_LENGTH = 0.0

    const val WRIST_ENCODER_MULTIPLY = 0.0
    const val WRIST_ENCODER_OFFSET = 0.0

    //ESTIMATES
    const val WRIST_SETPOINT_STOW = 0.89
    const val WRIST_SETPOINT_SHOOT = 0.89
    const val WRIST_SETPOINT_AMP = -0.80

    val field: AprilTagFieldLayout = AprilTagFields.k2023ChargedUp.loadAprilTagLayoutField()
    const val FIELD_WIDTH = 8.21
    const val FIELD_LENGTH = 16.54


    val rightPose = Transform3d(Translation3d(Units.inchesToMeters(-11.26), Units.inchesToMeters(-6.04), Units.inchesToMeters(9.25)), Rotation3d(0.0, 0.0, Units.degreesToRadians(-105.0)))
    val leftPose = Transform3d(Translation3d(Units.inchesToMeters(-11.26), Units.inchesToMeters(7.794),  Units.inchesToMeters(9.25)), Rotation3d(0.0, 0.0,  Units.degreesToRadians(105.0)))
    val centerPose = Transform3d(Translation3d(Units.inchesToMeters(-10.059), Units.inchesToMeters(6.081), Units.inchesToMeters(11.521)), Rotation3d(0.0, 0.349, PI))


}

object ConfigConstants{
    const val POWER_DEADBAND = 0.075
    const val ROT_DEADBAND = 0.075

    //Drive speed constants
    const val DRIVE_SPEED = 12.0
    const val DRIVE_SECONDARY_SPEED = 2.75
    const val ROT_SPEED = 10.0
    const val ROT_SECONDARY_SPEED = 2.75

    //Acceleration of drivetrain
    const val DRIVE_ACCEL = 30.0
    const val DRIVE_DECCEL = 36.0

    const val DRIVE_OPTIMIZED = true
}

object SwerveConstants{
    const val HALF_SIDE_LENGTH = 0.0
    const val DRIVE_BASE_RADIUS = 0.0

    const val POWER_S = 0.0
    const val POWER_V = 0.0
    const val POWER_A = 0.0

    const val POWER_P = 0.0
    const val POWER_I = 0.0
    const val POWER_D = 0.0

    const val ANGLE_P = 0.0
    const val ANGLE_I = 0.0
    const val ANGLE_D = 0.0

    val swerveModuleData = listOf(
            SwerveModuleData(Translation2d(HALF_SIDE_LENGTH, -HALF_SIDE_LENGTH), 4, 11, 12, 0.0, true), //Back Left
            SwerveModuleData(Translation2d(-HALF_SIDE_LENGTH, -HALF_SIDE_LENGTH), 5, 10, 13, 0.0, true), //Back Right
            SwerveModuleData(Translation2d(HALF_SIDE_LENGTH, HALF_SIDE_LENGTH), 6, 9, 14, 0.0, true), //Front Left
            SwerveModuleData(Translation2d(-HALF_SIDE_LENGTH, HALF_SIDE_LENGTH), 7, 8, 15, 0.0, true)) //Front Right

    // Pi * diameter / gear ratio
    const val POWER_ENCODER_MULTIPLY_POSITION = PI * 0.1016 / 6.75
    // Velocity is in rpm so needs / 60
    const val POWER_ENCODER_MULTIPLY_VELOCITY = POWER_ENCODER_MULTIPLY_POSITION / 60.0

    const val ANGLE_ENCODER_MULTIPLY = 0.01745329251

    const val MAX_AUTO_SPEED = 0.0

    const val AUTO_POWER_P = 0.0
    const val AUTO_POWER_I = 0.0
    const val AUTO_POWER_D = 0.0

    const val AUTO_ANGLE_P = 0.0
    const val AUTO_ANGLE_I = 0.0
    const val AUTO_ANGLE_D = 0.0

    const val AUTO_REPLANNING_TOTAL_ERROR = 0.0
    const val AUTO_REPLANNING_SPIKE = 0.0
}

object ElectronicIDs{
    const val INTAKE_MOTOR_ID = 3
    const val WRIST_ONE_ID = 2
    const val WRIST_TWO_ID = 1

    val camData = listOf(Pair("Center", PhysicalConstants.centerPose), Pair("Right2", PhysicalConstants.rightPose), Pair("Left2", PhysicalConstants.leftPose))
}

object RuntimeConstants{
    var motorSpeed = 0.0
    var wristSetPoint = 0.0
}

object TuningConstants {
    val defaultVisionDeviations: Matrix<N3, N1> = fill(Nat.N3(), Nat.N1(), 1.0, 1.0, 1000.0)
    val alignVisionDeviations: Matrix<N3, N1> = fill(Nat.N3(), Nat.N1(),3.0, 3.0, 1000.0)

    const val VISION_TIMEOUT = 0.1

    const val WRIST_P = 0.0
    const val WRIST_I = 0.0
    const val WRIST_D = 0.0

    //ESTIMATIONS
    const val WRIST_S = 0.0
    const val WRIST_G = 0.0
    const val WRIST_V = 0.0
    const val WRIST_A = 0.0

    const val WRIST_ANGLE_TOLERANCE = 0.0
    val trapConstraints = TrapezoidProfile.Constraints(1.0, 1.0)
}



