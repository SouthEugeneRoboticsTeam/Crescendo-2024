package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.ConfigConstants
import org.sert2521.crescendo2024.Input
import org.sert2521.crescendo2024.subsystems.Drivetrain
import kotlin.math.*

open class ReadJoysticks : Command() {
    /*
    This math just works, trust.
    It's a two dimensional slew rate limiter with a changing rate based on the
    value of [currAccelLimit] in m/s^2 (or at least in the same units as x and y).
    Basically it lets you have changing acceleration limits based on outside variables
    like elevator height, arm angle, button presses, etc.
    The acceleration limit you want for a cycle should be encoded into [currAccelLimit].
    If you want a constant acceleration limit just set that to a constant.
    Otherwise you can set it to whatever function produces your wanted acceleration limit.
    The Genius Kai Dassonville strikes again
     */

    val joystickX = Input::getX
    val joystickY = Input::getY
    val joystickZ = Input::getZ

    val inputRotOffset = ConfigConstants.INPUT_ROT_OFFSET

    private var lastX = 0.0
    private var lastY = 0.0

    private var x = 0.0
    private var y = 0.0

    private var angle = 0.0
    private var sqrMagnitude = 0.0

    private var newMagnitude = 0.0

    private var cubicChassisSpeeds = ChassisSpeeds()

    private var magChange = 0.0
    private var magFraction = 0.0

    private var appliedAccelLimit = 0.0

    init{
        addRequirements(Drivetrain)
    }

    // Oh man I bet this could be optimized but I'm eeeepy...
    public fun readJoysticks(
        accelLimit:Double, rotOffset: Rotation2d,
        deccelLimit:Double = accelLimit,
        maxSpeed:Double = ConfigConstants.DRIVE_SPEED, fieldOriented:Boolean=true):ChassisSpeeds{
        /**
         * Returns the desired directions as a triplet of X, Y, and Rotation in robot coordinates
         * Should be called periodically.
         */
        x = joystickX()
        y = joystickY()

        if (x==0.0 && y==0.0){
            appliedAccelLimit = deccelLimit
        } else {
            appliedAccelLimit = accelLimit
        }

        angle = atan2(y, x)
        sqrMagnitude = x.pow(2) + y.pow(2)

        // Deadband + cubic curve
        newMagnitude = sqrt(
            MathUtil.applyDeadband(sqrMagnitude, ConfigConstants.CONTROLLER_DEADBAND.pow(2))
        ).pow(3)


        if (fieldOriented){
            cubicChassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
                sin(angle) * newMagnitude * maxSpeed,
                cos(angle) * newMagnitude * maxSpeed,
                joystickZ().pow(3) * ConfigConstants.ROT_SPEED,
                Drivetrain.getPose().rotation.minus(rotOffset)
            )
        } else {
            cubicChassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
                sin(angle) * newMagnitude * maxSpeed,
                cos(angle) * newMagnitude * maxSpeed,
                joystickZ().pow(3) * ConfigConstants.ROT_SPEED,
                Rotation2d()
            )
        }
        // X and Y are swapped because Y is left in robot coordinates and X is up
        // It's the other way around in controller coordinates


        // Total magnitude of change since last cycle
        // NOTE: NOT change of total magnitude: it is magnitude of change in 2D coordinates
        magChange = sqrt((lastX - cubicChassisSpeeds.vxMetersPerSecond).pow(2) + (lastY-cubicChassisSpeeds.vyMetersPerSecond).pow(2))

        // The fraction of the change in magnitude that should be applied
        magFraction = 1.0

        // Applies maximum magnitude of change of x and y
        // (divide by 50 so that currAccel can be in m/s^2)
        if (magChange > appliedAccelLimit/50.0){
            magFraction = (appliedAccelLimit/50.0)/magChange
        }

        lastX = MathUtil.interpolate(lastX, cubicChassisSpeeds.vxMetersPerSecond, magFraction)
        lastY = MathUtil.interpolate(lastY, cubicChassisSpeeds.vyMetersPerSecond, magFraction)

        return ChassisSpeeds(lastX, lastY, cubicChassisSpeeds.omegaRadiansPerSecond)
    }

    fun readChassisSpeeds(fieldChassisSpeeds: ChassisSpeeds,
                          accelLimit: Double, rotOffset:Rotation2d):ChassisSpeeds{
        /**
         * Same as [readJoysticks] but has the speeds passed in through ChassisSpeeds.
         * Outputs calculated ChassisSpeeds once the acceleration limit has been calculated
         */

        cubicChassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
            fieldChassisSpeeds,
            Drivetrain.getPose().rotation.minus(rotOffset)
        )

        // Total magnitude of change since last cycle
        // NOTE: NOT change of total magnitude: it is magnitude of change in 2D coordinates
        magChange = sqrt((lastX - cubicChassisSpeeds.vxMetersPerSecond).pow(2) + (lastY-cubicChassisSpeeds.vyMetersPerSecond).pow(2))

        // The fraction of the change in magnitude that should be applied
        magFraction = 1.0

        // Applies maximum magnitude of change of x and y
        // (divide by 50 so that currAccel can be in m/s^2)
        if (magChange > accelLimit/50.0){
            magFraction = (accelLimit/50.0)/magChange
        }

        lastX = MathUtil.interpolate(lastX, cubicChassisSpeeds.vxMetersPerSecond, magFraction)
        lastY = MathUtil.interpolate(lastY, cubicChassisSpeeds.vyMetersPerSecond, magFraction)

        // X and Y are swapped because Y is left in robot coordinates and X is up
        // It's the other way around in controller coordinates
        return ChassisSpeeds(lastX, lastY, fieldChassisSpeeds.omegaRadiansPerSecond)
    }
}
