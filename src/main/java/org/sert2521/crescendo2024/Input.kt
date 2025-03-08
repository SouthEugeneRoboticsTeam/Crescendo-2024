package org.sert2521.crescendo2024

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj2.command.Commands.runOnce
import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import edu.wpi.first.wpilibj2.command.button.Trigger
import org.sert2521.crescendo2024.commands.*
import org.sert2521.crescendo2024.subsystems.Drivetrain

//TODO: Label buttons
object Input {
    private val driverController = CommandXboxController(0)
    private val gunnerController = Joystick(1)

    private val robotOrientedMode = Trigger{driverController.leftTriggerAxis>0.3}


    private val intake = driverController.rightBumper()
    private val resetAngle = driverController.start()

    private val intakeReverse = driverController.x()
    private val rev = driverController.rightTrigger(0.3)
    private val sourceIntake = driverController.b()
    private val rezeroNote = driverController.y()

    private val shoot = driverController.leftBumper()

    init{
        intake.whileTrue(IntakeCommand())
        //intakeReverse.whileTrue(IntakeReverse())
        rev.whileTrue(SetFlywheel(ConfigConstants.FLYWHEEL_SHOOT_SPEED))

        robotOrientedMode.whileTrue(Drivetrain.drive(JoystickCommand()))

        sourceIntake.whileTrue(SetFlywheel(-4000.0))
        sourceIntake.onFalse(RezeroNote())//.alongWith(SetFlywheel(ConfigConstants.FLYWHEEL_IDLE_SPEED)))
        rezeroNote.whileTrue(RezeroNote())
        resetAngle.onTrue(runOnce({ Drivetrain.setNewPose(Pose2d()) }))

        shoot.whileTrue(Outtake())
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
        return -driverController.leftX
    }

    fun getY(): Double {
        return -driverController.leftY
    }

    fun getZ(): Double {
        return driverController.rightX
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