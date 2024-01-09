package org.sert2521.crescendo2024

import edu.wpi.first.math.geometry.Translation2d
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

}

object SwerveConstants{
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
            SwerveModuleData(Translation2d(PhysicalConstants.HALF_SIDE_LENGTH, -PhysicalConstants.HALF_SIDE_LENGTH), -1, -1, -1, 0.0, true), //Back Left
            SwerveModuleData(Translation2d(-PhysicalConstants.HALF_SIDE_LENGTH, -PhysicalConstants.HALF_SIDE_LENGTH), -1, -1, -1, 0.0, true), //Back Right
            SwerveModuleData(Translation2d(PhysicalConstants.HALF_SIDE_LENGTH, PhysicalConstants.HALF_SIDE_LENGTH), -1, -1, -1, 0.0, true), //Front Left
            SwerveModuleData(Translation2d(-PhysicalConstants.HALF_SIDE_LENGTH, PhysicalConstants.HALF_SIDE_LENGTH), -1, -1, -1, 0.0, true)) //Front Right

}



