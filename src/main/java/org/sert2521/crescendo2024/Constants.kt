package org.sert2521.crescendo2024

import edu.wpi.first.math.geometry.Translation2d

/*
 * The Constants file provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This file should not be used for any other purpose.
 * All String, Boolean, and numeric (Int, Long, Float, Double) constants should use
 * `const` definitions. Other constant types should use `val` definitions.
 */
class SwerveModuleData(val position: Translation2d, val powerMotorID: Int, val angleMotorID: Int, val angleEncoderID: Int, val angleOffset: Double, val inverted: Boolean)

object PhysicalConstants{
    const val HALF_SIDE_LENGTH = 0.0

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
}

object SwerveConstants{
    const val HALF_SIDE_LENGTH = 0.0

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
            SwerveModuleData(Translation2d(HALF_SIDE_LENGTH, -HALF_SIDE_LENGTH), -1, -1, -1, 0.0, true), //Back Left
            SwerveModuleData(Translation2d(-HALF_SIDE_LENGTH, -HALF_SIDE_LENGTH), -1, -1, -1, 0.0, true), //Back Right
            SwerveModuleData(Translation2d(HALF_SIDE_LENGTH, HALF_SIDE_LENGTH), -1, -1, -1, 0.0, true), //Front Left
            SwerveModuleData(Translation2d(-HALF_SIDE_LENGTH, HALF_SIDE_LENGTH), -1, -1, -1, 0.0, true)) //Front Right

}



