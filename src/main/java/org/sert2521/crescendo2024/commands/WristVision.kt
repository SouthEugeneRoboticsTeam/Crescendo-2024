package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.filter.Debouncer
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Vision
import org.sert2521.crescendo2024.subsystems.Wrist
import kotlin.math.PI

class WristVision(val debounce:Boolean=false) : Command() {

    var prevWristTarget = PhysicalConstants.WRIST_SETPOINT_STOW
    var currWristTarget = PhysicalConstants.WRIST_SETPOINT_STOW
    var drivetrainTarget: Rotation2d? = Rotation2d(0.0)
    var wristCommand:Command = SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW, false).asProxy()
    var commandedWristAngle = PhysicalConstants.WRIST_SETPOINT_STOW
    var endFilter = Debouncer(0.2)
    //DOES NOT MEAN TRAP AS IN THE STAGE THINGY
    var wristIsTrap = true
    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Vision)
    }

    override fun initialize() {
        currWristTarget = Vision.getVisionWristAngle()
        drivetrainTarget = Vision.getDriveAngleTarget()
        currWristTarget=Vision.getVisionWristAngle()
        RuntimeConstants.wristVision = currWristTarget
        wristCommand = SetWrist(currWristTarget).asProxy()
        wristCommand.schedule()
        wristIsTrap = true
    }

    override fun execute() {
        currWristTarget= Vision.getVisionWristAngle()
        RuntimeConstants.wristVision = currWristTarget

        if (wristIsTrap){
            if (wristCommand.isScheduled){
                wristIsTrap = true
            } else {
                wristCommand = SetWrist(currWristTarget, false, true).asProxy()
                wristCommand.schedule()
                wristIsTrap = false
            }
        } else {
            if (!wristCommand.isScheduled){
                wristCommand = SetWrist(currWristTarget, false, true).asProxy()
                wristCommand.schedule()
                wristIsTrap = false
            }
        }

    }

    override fun isFinished(): Boolean {
        // TODO: Make this not goofy
        if (!debounce){
            return Wrist.getRadians()>currWristTarget-TuningConstants.VISION_WRIST_TOLERANCE && Wrist.getRadians()<currWristTarget+TuningConstants.VISION_WRIST_TOLERANCE && !RuntimeConstants.isVisionAuto
        }
        val filterResult = endFilter.calculate(Wrist.getRadians()>currWristTarget-TuningConstants.VISION_WRIST_TOLERANCE && Wrist.getRadians()<currWristTarget+TuningConstants.VISION_WRIST_TOLERANCE && !RuntimeConstants.isVisionAuto)
        if (filterResult && !(Wrist.getRadians()>currWristTarget-TuningConstants.VISION_WRIST_TOLERANCE && Wrist.getRadians()<currWristTarget+TuningConstants.VISION_WRIST_TOLERANCE && !RuntimeConstants.isVisionAuto)){
            endFilter = Debouncer(0.2)
            return false
        }
        return filterResult
    }

    override fun end(interrupted: Boolean) {
        RuntimeConstants.visionAligning = false
    }
}
