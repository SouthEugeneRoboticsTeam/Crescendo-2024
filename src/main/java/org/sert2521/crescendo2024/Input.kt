package org.sert2521.crescendo2024

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import org.sert2521.crescendo2024.commands.*
import org.sert2521.crescendo2024.subsystems.Climber
import org.sert2521.crescendo2024.subsystems.Drivetrain
import org.sert2521.crescendo2024.subsystems.Wrist
import java.io.ObjectInputFilter.Config


object Input {
    private val driverController = XboxController(0)
    private val gunnerController = Joystick(1)

    private val intake = JoystickButton(gunnerController, 1)
    private val intakeReverse = JoystickButton(gunnerController, 2)
    private val rev = JoystickButton(gunnerController, 3)
    private val outtake = JoystickButton(driverController, 6)
    private val wristStow = JoystickButton(gunnerController, 7)
    private val wristAmp = JoystickButton(gunnerController, 5)
    private val wristPodium = JoystickButton(gunnerController, 6)
    private val wristFar = JoystickButton(gunnerController, 4)
    private val manualUp = JoystickButton(gunnerController, 10)
    private val manualDown = JoystickButton(gunnerController, 8)
    private val climb = JoystickButton(gunnerController, 9)
    private val visionAlign = JoystickButton(driverController, 1)
    private val resetAngle = JoystickButton(driverController, 4)
    private val secondarySpeedButton = JoystickButton(driverController, 2)
    init{
        intake.whileTrue(IntakeCommand())
        intakeReverse.whileTrue(IntakeReverse())
        rev.whileTrue(SetFlywheel(ConfigConstants.FLYWHEEL_SHOOT_SPEED))
        outtake.whileTrue(Outtake())

        wristStow.onTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW, false))
        wristAmp.onTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_AMP, false))
        wristPodium.onTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM, false))
        wristFar.onTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_FAR, false))

        manualUp.whileTrue(SetClimb(0.5))
        manualDown.whileTrue(SetClimb(-0.5))

        climb.onTrue(InstantCommand({ Drivetrain.enterClimbPos() }))
        climb.whileTrue(ClimbInitiate())
        // Make these do stuff
        // manualUp.whileTrue()
        // manualDown.whileTrue()
        // visionAlign.whileTrue()
        resetAngle.onTrue(InstantCommand({ Drivetrain.setNewPose(Pose2d())}))
        //secondarySpeedButton.onTrue(InstantCommand({ secondarySpeedMode = !secondarySpeedMode }))
        //secondarySpeedButton.onFalse(InstantCommand({ secondarySpeedMode = !secondarySpeedMode }))
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
        return -driverController.leftY
    }

    fun getY(): Double {
        return -driverController.leftX
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
}