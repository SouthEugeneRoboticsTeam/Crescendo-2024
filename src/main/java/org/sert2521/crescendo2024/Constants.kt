package org.sert2521.crescendo2024

import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.util.Units
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

object PhysicalConstants{

    const val HALF_SIDE_LENGTH = 0.0

    const val WRIST_ENCODER_MULTIPLY = -2*PI
    var WRIST_ENCODER_OFFSET = 1.716-0.197 //current angle with zero offset - normal stow position

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
    const val WRIST_SETPOINT_PARALLEL_PASS = 0.65

    init {

    }

    val centerPose = Transform3d(Translation3d(Units.inchesToMeters(-10.029), Units.inchesToMeters(6.081), Units.inchesToMeters(15.26)), Rotation3d(0.0, 0.349, PI))



    const val FLYWHEEL_GEAR_RATIO = 3.0/2.0
}

object ConfigConstants{
    const val CONTROLLER_DEADBAND = 0.05

    //Drive speed constants
    const val DRIVE_SPEED = 1.2
    const val DRIVE_SECONDARY_SPEED = 2.25
    const val ROT_SPEED = 6.0
    const val ROT_SECONDARY_SPEED = 2.25

    //Acceleration of drivetrain
    const val DRIVE_ACCEL = 35.0
    const val DRIVE_DECCEL = 40.0

    const val DRIVE_OPTIMIZED = true

    const val INPUT_ROT_OFFSET = 0.0

    const val FLYWHEEL_IDLE_SPEED = 0.0
    const val FLYWHEEL_SHOOT_SPEED = 3000.0 // 1000.0
}

object SwerveConstants{
    const val HALF_SIDE_LENGTH = 0.263525
    const val DRIVE_BASE_RADIUS = 0.37268

    const val DRIVE_S = 0.0
    const val DRIVE_V = 0.2  //0.2
    const val POWER_A = 0.0

    const val DRIVE_P = 0.0 //0.05
    const val DRIVE_I = 0.0
    const val DRIVE_D = 0.0

    const val ANGLE_P = 1.4
    const val ANGLE_I = 0.0
    const val ANGLE_D = 0.0

    const val VISION_DRIVE_P = 0.64
    const val VISION_DRIVE_I = 0.00
    const val VISION_DRIVE_D = 0.00

    const val VISION_ANGLE_P = 0.01
    const val VISION_ANGLE_I = 0.01
    const val VISION_ANGLE_D = 0.00

    val swerveModuleData = listOf(
            SwerveModuleData(Translation2d(HALF_SIDE_LENGTH, -HALF_SIDE_LENGTH), 5, 7, 16, -0.355-1.61-1.56+PI/2, false), //Back Left
            SwerveModuleData(Translation2d(-HALF_SIDE_LENGTH, -HALF_SIDE_LENGTH), 1, 2, 15, -0.138-1.57-1.54+PI/2, false), //Back Right
            SwerveModuleData(Translation2d(HALF_SIDE_LENGTH, HALF_SIDE_LENGTH), 16, 15, 14, 2.41-1.612-1.58+PI/2, false), //Front Left
            SwerveModuleData(Translation2d(-HALF_SIDE_LENGTH, HALF_SIDE_LENGTH), 3, 12, 13, 0.059-1.568-1.575+PI/2, false)) //Front Right

    // Pi * diameter / gear ratio
    const val DRIVE_ENCODER_MULTIPLY_POSITION = PI * 0.1016 / 5.903
    // Velocity is in rpm so needs / 60
    const val DRIVE_ENCODER_MULTIPLY_VELOCITY = DRIVE_ENCODER_MULTIPLY_POSITION / 60.0


    const val ANGLE_ABSOLUTE_ENCODER_MULTIPLY = 2* PI

    const val ANGLE_MOTOR_ENCODER_MULTIPLY = 1/21.4285714 * (2*PI)

    const val DRIVE_MOTOR_INVERTED = false
    const val ANGLE_MOTOR_INVERTED = false

    const val DRIVE_CURRENT_LIMIT = 40
    const val ANGLE_CURRENT_LIMIT = 40

}

object ElectronicIDs{
    const val INTAKE_MOTOR_ID = 10
    const val INTAKE_ALIGNMENT_MOTOR_ID = 13
    const val WRIST_ONE_ID = 17
    const val WRIST_TWO_ID = 4
    const val INDEXER_MOTOR_ID = 9
    const val BEAMBREAK_ID = 4
    const val FLYWHEEL_MOTOR_ONE_ID = 8
    const val FLYWHEEL_MOTOR_TWO_ID = 11
    const val ABSOLUTE_ENCODER_ID = 2

    //val camData:Pair<String, Translation3d> = listOf(/*Pair("Center", PhysicalConstants.centerPose), Pair("Right2", PhysicalConstants.rightPose), Pair("Left2", PhysicalConstants.frontPose)*/)
}

object RuntimeConstants{
    var motorSpeed = 0.0
    var wristSetPoint = PhysicalConstants.WRIST_SETPOINT_STOW
    var flywheelRevved = false
    var flywheelGoal = 0.0
}

object TuningConstants {
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
        wristAngLookup.put(3.48, 0.32)
        wristAngLookup.put(3.74, 0.35)
        wristAngLookup.put(4.025, 0.365)
        wristAngLookup.put(4.49, 0.387)
        wristAngLookup.put(4.82, 0.4)
        wristAngLookup.put(5.25, 0.423)
        wristAngLookup.put(5.74, 0.43)
        wristAngLookup.put(6.53, 0.449)
    }

    const val WRIST_P = 30.0
    const val WRIST_I = 0.0
    const val WRIST_D = 0.0

    //ESTIMATIONS
    const val WRIST_S = 0.0
    const val WRIST_G = 0.365
    const val WRIST_V = 0.0
    const val WRIST_A = 0.0

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

    const val FLYWHEEL_BB_AGGRO = 1.0

}

object VisionTargetPositions {

    val reefPositions = mutableListOf(
        Pose2d(3.2, 4.19, Rotation2d(0.0)),
        Pose2d(3.2, 3.86, Rotation2d(0.0)),

        Pose2d(3.7, 2.99, Rotation2d(PI/3)),
        Pose2d(3.99, 2.83, Rotation2d(PI/3)),

        Pose2d(4.99, 2.83, Rotation2d((2.0*PI)/3.0)),
        Pose2d(5.28, 2.98, Rotation2d((2.0*PI)/3.0)),

        Pose2d(5.78, 3.86, Rotation2d(PI)),
        Pose2d(5.78, 4.19, Rotation2d(PI)),

        Pose2d(5.28, 5.07, Rotation2d((-2.0*PI)/3.0)),
        Pose2d(4.99, 5.23, Rotation2d((-2.0*PI)/3.0)),

        Pose2d(3.99, 5.23, Rotation2d(-PI/3.0)),
        Pose2d(3.70, 5.07, Rotation2d(-PI/3.0)),
    )

}

class SwerveModuleData(val position: Translation2d, val driveMotorID: Int, val angleMotorID: Int, val angleEncoderID: Int, val angleOffset: Double, val inverted: Boolean){}