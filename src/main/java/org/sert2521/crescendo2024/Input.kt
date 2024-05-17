package org.sert2521.crescendo2024

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.WaitCommand
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import edu.wpi.first.wpilibj2.command.button.Trigger
import org.sert2521.crescendo2024.commands.*
import org.sert2521.crescendo2024.subsystems.Climber
import org.sert2521.crescendo2024.subsystems.Drivetrain
import org.sert2521.crescendo2024.subsystems.Indexer
import org.sert2521.crescendo2024.subsystems.Wrist
import java.io.ObjectInputFilter.Config


object Input {
    private val driverController = XboxController(0)
    private val gunnerController = Joystick(1)

    private val intake = JoystickButton(driverController, 6)
    private val visionAlign = JoystickButton(driverController, 1)
    private val resetAngleOne = JoystickButton(driverController, 7)
    private val resetAngleTwo = JoystickButton(driverController, 8)
    // private val secondarySpeedButton = JoystickButton(driverController, 2)
    //private val wristParallelPass = JoystickButton(driverController, 5)
    private val testButton = JoystickButton(driverController, 2)


    private val intakeReverse = JoystickButton(gunnerController, 2)
    //private val rev = Trigger({driverController.rightTriggerAxis>0.3})
    private val outtake = JoystickButton(driverController, 5)
    private val wristStow = JoystickButton(gunnerController, 7)
    // private val wristAmp = JoystickButton(gunnerController, 5)
    private val wristPodium = JoystickButton(gunnerController, 6)
    private val wristAmpRev = JoystickButton(gunnerController, 4)
    private val manualUp = JoystickButton(gunnerController, 13)
    private val manualDown = JoystickButton(gunnerController, 11)
    private val climb = JoystickButton(gunnerController, 12)
    private val resetWrist = JoystickButton(gunnerController, 8)
    private val armUp = JoystickButton(gunnerController, 15)
    private val armDown = JoystickButton(gunnerController, 16)
    private val sourceIntake = JoystickButton(gunnerController, 10)
    private val rezeroNote = JoystickButton(gunnerController, 9)
    private val passRev = Trigger{ gunnerController.pov==0 }


    private val rumble = Trigger({ Indexer.getBeamBreak() })
    init{
        intake.whileTrue(IntakeCommand())
        intakeReverse.whileTrue(IntakeReverse())
        //rev.whileTrue(SetFlywheel(ConfigConstants.FLYWHEEL_SHOOT_SPEED))
        //rev.onFalse(SetFlywheel(ConfigConstants.FLYWHEEL_IDLE_SPEED))
        outtake.whileTrue(Outtake())

        wristStow.onTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW))
        // commented out for demo mode
        // wristAmp.onTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_AMP))
        wristPodium.onTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM))
        wristAmpRev.whileTrue(SetFlywheel(2000.0))
        //wristAmpRev.onFalse(SetFlywheel(ConfigConstants.FLYWHEEL_IDLE_SPEED))

        //wristParallelPass.onTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_PARALLEL_PASS))
        //wristParallelPass.onFalse(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW))

        manualUp.whileTrue(SetClimb(1.0))
        manualDown.whileTrue(SetClimb(-1.0))

        climb.onTrue(InstantCommand({ Drivetrain.enterClimbPos() }))
        climb.whileTrue(ClimbInitiate())
        // Make these do stuff
        //TODO:UNBIND THE MANUAL ARM
        armUp.whileTrue(ManualArmCommand(0.5))
        armDown.whileTrue(ManualArmCommand(-0.5))
        //sourceIntake.onTrue(SetWrist(PhysicalConstants.WRIST_SETPOINT_SOURCE))
        sourceIntake.whileTrue(SetFlywheel(-4000.0))
        sourceIntake.onFalse(RezeroNote())//.alongWith(SetFlywheel(ConfigConstants.FLYWHEEL_IDLE_SPEED)))
        rezeroNote.whileTrue(RezeroNote())
        //manualUp.whileTrue(ManualArmCommand(0.2))
        //manualDown.whileTrue(ManualArmCommand(-0.2))
        // visionAlign.whileTrue()
        resetAngleOne.and(resetAngleTwo::getAsBoolean).onTrue(InstantCommand({ Drivetrain.setNewPose(Pose2d()) }))

        //resetAngle.onTrue(InstantCommand({ Drivetrain.setNewPose(Pose2d()) }))
        rumble.onTrue(InstantCommand({setRumble(0.8)}).andThen(WaitCommand(0.2).andThen(InstantCommand({ setRumble(0.0) }))))
        //testButton.onTrue(InstantCommand({Drivetrain.setNewVisionPose(Pose2d(2.0, 3.0, Rotation2d(0.0)))}))
        resetWrist.whileTrue(ResetWrist())
        visionAlign.whileTrue(VisionAlign())
        visionAlign.onFalse(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW))
        passRev.whileTrue(SetFlywheel(3000.0))
        //passRev.onFalse(SetFlywheel(ConfigConstants.FLYWHEEL_IDLE_SPEED))
        //visionAlign.onTrue(InstantCommand({Wrist.rezeroEncoder()}))
        testButton.onTrue(InstantCommand({Drivetrain.setNewVisionPose(Pose2d(4.0, 6.0, Rotation2d(1.0)))}))

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

    fun setRumble(amount: Double) {
        driverController.setRumble(GenericHID.RumbleType.kBothRumble, amount)
    }
}