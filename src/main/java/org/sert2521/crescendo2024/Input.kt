package org.sert2521.crescendo2024

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.Commands.runOnce
import edu.wpi.first.wpilibj2.command.WaitCommand
import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import edu.wpi.first.wpilibj2.command.button.Trigger
import org.sert2521.crescendo2024.commands.*
import org.sert2521.crescendo2024.subsystems.Drivetrain

//TODO: Label buttons
object Input {
    private val driverController = CommandXboxController(0)

    private var flywheelTrigger = driverController::getRightTriggerAxis
    private var fire = driverController.a()
    private var flywheelIntake = driverController.leftBumper()
    private var resetNote = driverController.x()
    private val flywheelShoot = Trigger{ driverController.rightTriggerAxis > 0.1 }

    init{
        flywheelIntake.whileTrue(SetFlywheel(ConfigConstants.FLYWHEEL_REVERSE_SPEED))
        fire.whileTrue(Outtake())
        resetNote.onTrue(RezeroNote())
        flywheelShoot.whileTrue(SetFlywheel(ConfigConstants.FLYWHEEL_MAX_SPEED))
    }

    fun getRightTrigger(deadband: Double): Double {
        return flywheelTrigger()
    }

    var secondarySpeedMode = false

    fun getSecondarySpeed(): Double {
        return if (!secondarySpeedMode) {
            driverController.leftTriggerAxis
        } else {
            1.0
        }
    }

    fun getX(): Double {
        return 0.0  // -driverController.leftY
    }

    fun getY(): Double {
        return 0.0  // -driverController.leftX
    }

    fun getRot(): Double {
        return -driverController.rightX
    }

    fun getColor(): DriverStation.Alliance {
        return if (DriverStation.getAlliance().isEmpty){
            DriverStation.Alliance.Blue
        } else {
            DriverStation.getAlliance().get()
        }
    }

    fun setRumble(amount: Double) {
        driverController.setRumble(GenericHID.RumbleType.kBothRumble, amount)
    }
}