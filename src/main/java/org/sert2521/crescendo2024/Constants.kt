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
import com.pathplanner.lib.auto.NamedCommands
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.commands.IntakeCommand
import org.sert2521.crescendo2024.commands.Outtake
import org.sert2521.crescendo2024.commands.SetFlywheel
import org.sert2521.crescendo2024.commands.SetWrist
import java.io.ObjectInputFilter.Config
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
    const val WRIST_ENCODER_OFFSET = -1.789203

    //ESTIMATES
    const val WRIST_SETPOINT_STOW = -0.21
    const val WRIST_SETPOINT_AMP = 1.53
    const val WRIST_SETPOINT_PODIUM = 0.15
    const val WRIST_SETPOINT_FAR = 0.375

    val field: AprilTagFieldLayout = AprilTagFields.k2023ChargedUp.loadAprilTagLayoutField()
    const val FIELD_WIDTH = 8.21
    const val FIELD_LENGTH = 16.54


    val rightPose = Transform3d(Translation3d(Units.inchesToMeters(-11.26), Units.inchesToMeters(-6.04), Units.inchesToMeters(9.25)), Rotation3d(0.0, 0.0, Units.degreesToRadians(-105.0)))
    val frontPose = Transform3d(Translation3d(Units.inchesToMeters(18.375), Units.inchesToMeters(0.0),  Units.inchesToMeters(5.485)), Rotation3d(0.0, 0.0,  0.0))
    val centerPose = Transform3d(Translation3d(Units.inchesToMeters(-10.059), Units.inchesToMeters(6.081), Units.inchesToMeters(11.521)), Rotation3d(0.0, 0.349, PI))

    const val FLYWHEEL_GEAR_RATIO = 3.0/2.0

    const val CLIMBER_ENCODER_TO_METERS = 1.0

    const val CLIMBER_BETWEEN_DISTANCE = 1.0
}

object ConfigConstants{
    const val POWER_DEADBAND = 0.075
    const val ROT_DEADBAND = 0.075

    //Drive speed constants
    const val DRIVE_SPEED = 5.3
    const val DRIVE_SECONDARY_SPEED = 2.75
    const val ROT_SPEED = 4.0
    const val ROT_SECONDARY_SPEED = 2.75

    //Acceleration of drivetrain
    const val DRIVE_ACCEL = 100.0
    const val DRIVE_DECCEL = 100.0

    const val DRIVE_OPTIMIZED = true

    const val FLYWHEEL_IDLE_SPEED = 0.0
    const val FLYWHEEL_SHOOT_SPEED = 4000.0
}

object SwerveConstants{
    const val HALF_SIDE_LENGTH = 0.263525
    const val DRIVE_BASE_RADIUS = 0.37268

    const val POWER_S = 0.0
    const val POWER_V = 0.2
    const val POWER_A = 0.0

    const val POWER_P = 0.3
    const val POWER_I = 0.0
    const val POWER_D = 0.0

    const val ANGLE_P = 1.4
    const val ANGLE_I = 0.0
    const val ANGLE_D = 0.0

    val swerveModuleData = listOf(
            SwerveModuleData(Translation2d(HALF_SIDE_LENGTH, -HALF_SIDE_LENGTH), 5, 7, 16, -0.355-PI/2, false), //Back Left
            SwerveModuleData(Translation2d(-HALF_SIDE_LENGTH, -HALF_SIDE_LENGTH), 1, 2, 15, -0.138-PI/2, false), //Back Right
            SwerveModuleData(Translation2d(HALF_SIDE_LENGTH, HALF_SIDE_LENGTH), 16, 15, 14, 2.41-PI/2, false), //Front Left
            SwerveModuleData(Translation2d(-HALF_SIDE_LENGTH, HALF_SIDE_LENGTH), 3, 12, 13, 0.059-PI/2, false)) //Front Right

    // Pi * diameter / gear ratio
    const val POWER_ENCODER_MULTIPLY_POSITION = PI * 0.1016 / 5.903
    // Velocity is in rpm so needs / 60
    const val POWER_ENCODER_MULTIPLY_VELOCITY = POWER_ENCODER_MULTIPLY_POSITION / 60.0


    const val ANGLE_ENCODER_MULTIPLY = 2* PI

    const val ANGLE_MOTOR_ENCODER_MULTIPLY = 1/21.4285714 * (2*PI)

    const val MAX_AUTO_SPEED = 2.0

    const val AUTO_POWER_P = 0.2
    const val AUTO_POWER_I = 0.0
    const val AUTO_POWER_D = 0.0

    const val AUTO_ANGLE_P = 1.4
    const val AUTO_ANGLE_I = 0.0
    const val AUTO_ANGLE_D = 0.0

    const val AUTO_REPLANNING_TOTAL_ERROR = 0.0
    const val AUTO_REPLANNING_SPIKE = 0.0

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
    var wristSetPoint = 0.0
    var flywheelRevved = false
    var flywheelGoal = 0.0
}

object TuningConstants {
    val defaultVisionDeviations: Matrix<N3, N1> = fill(Nat.N3(), Nat.N1(), 1.0, 1.0, 1000.0)
    val alignVisionDeviations: Matrix<N3, N1> = fill(Nat.N3(), Nat.N1(),3.0, 3.0, 1000.0)

    const val VISION_TIMEOUT = 0.1

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
