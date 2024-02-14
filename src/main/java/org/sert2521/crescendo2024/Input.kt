package org.sert2521.crescendo2024

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import org.sert2521.crescendo2024.commands.IntakeCommand
import org.sert2521.crescendo2024.commands.Outtake
import org.sert2521.crescendo2024.commands.SetFlywheel
import org.sert2521.crescendo2024.commands.SetWrist
import org.sert2521.crescendo2024.subsystems.Drivetrain
import org.sert2521.crescendo2024.subsystems.Wrist
import java.io.ObjectInputFilter.Config


object Input {
    private val driverController = XboxController(0)
    private val gunnerController = Joystick(1)

    private val intake = JoystickButton(gunnerController, 1)
    private val rev = JoystickButton(gunnerController, 8)
    private val outtake = JoystickButton(gunnerController, 3)
    private val wristStow = JoystickButton(gunnerController, 7)
    private val wristAmp = JoystickButton(gunnerController, 3)
    private val manualUp = JoystickButton(gunnerController, 6)
    private val manualDown = JoystickButton(gunnerController, 10)
    private val visionAlign = JoystickButton(driverController, 1)
    private val resetAngle = JoystickButton(driverController, 4)
    private val secondarySpeedButton = JoystickButton(driverController, 2)
    init{
        intake.whileTrue(IntakeCommand())
        rev.whileTrue(SetFlywheel(ConfigConstants.FLYWHEEL_SHOOT_SPEED))
        outtake.whileTrue(Outtake())
        wristStow.whileTrue(SetWrist(0.2, false))
        //wristStow.whileTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW, true))
        //wristAmp.whileTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_AMP, true))
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