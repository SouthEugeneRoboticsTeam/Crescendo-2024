package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.subsystems.Vision

class WristVision : Command() {

    var prevWristTarget = PhysicalConstants.WRIST_SETPOINT_STOW
    var currWristTarget = PhysicalConstants.WRIST_SETPOINT_STOW
    var drivetrainTarget: Rotation2d? = Rotation2d(0.0)
    var wristCommand:Command = SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW, false)
    var commandedWristAngle = PhysicalConstants.WRIST_SETPOINT_STOW
    //DOES NOT MEAN TRAP AS IN THE STAGE THINGY
    var wristIsTrap = true
    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Vision)
    }

    override fun initialize() {}

    override fun execute() {
        currWristTarget= Vision.getVisionWristAngle()
        RuntimeConstants.wristVision = currWristTarget

        if (wristIsTrap){
            if (wristCommand.isScheduled){
                println("Scheduled")
                wristIsTrap = true
            } else {
                println("Not Scheduled")
                wristCommand = SetWrist(currWristTarget, false, true)
                wristCommand.schedule()
                wristIsTrap = false
            }
        } else {
            if (!wristCommand.isScheduled){
                wristCommand = SetWrist(currWristTarget, false, true)
                wristCommand.schedule()
                wristIsTrap = false
            }
        }
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return false
    }

    override fun end(interrupted: Boolean) {
        RuntimeConstants.visionAligning = false
        SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).schedule()
    }
}
