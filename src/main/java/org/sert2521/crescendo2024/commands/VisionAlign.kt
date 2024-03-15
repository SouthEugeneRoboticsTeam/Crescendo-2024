package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.*
import kotlin.math.PI
import kotlin.math.abs

class VisionAlign() : Command() {
    var prevWristTarget = PhysicalConstants.WRIST_SETPOINT_STOW
    var currWristTarget = PhysicalConstants.WRIST_SETPOINT_STOW
    var drivetrainTarget:Rotation2d? = Rotation2d(0.0)
    var wristCommand:Command = SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW, false)
    var commandedWristAngle = PhysicalConstants.WRIST_SETPOINT_STOW
    //DOES NOT MEAN TRAP AS IN THE STAGE THINGY
    var wristIsTrap = false

    val driveAlignPID = PIDController(TuningConstants.VISION_ALIGN_P, TuningConstants.VISION_ALIGN_I, TuningConstants.VISION_ALIGN_D)

    init {

    }

    override fun initialize() {
        currWristTarget = Vision.getVisionWristAngle()
        drivetrainTarget = Vision.getDriveAngleTarget()
    }

    override fun execute() {
        drivetrainTarget = Vision.getDriveAngleTarget()
        if (drivetrainTarget == null){
            RuntimeConstants.disableDriverRotation = false
        } else {
            RuntimeConstants.disableDriverRotation = true
            //Maybe square it or smth
            RuntimeConstants.visionRightStick = driveAlignPID.calculate(drivetrainTarget!!.radians-Drivetrain.getPose().rotation.radians)
        }

        currWristTarget=Vision.getVisionWristAngle()
        RuntimeConstants.wristVision = currWristTarget

        if (wristIsTrap){
            if (wristCommand.isScheduled){
                wristIsTrap = true
            } else {
                wristIsTrap = false
            }
        } else {
            if (wristCommand.isScheduled){
                //if gap is too big between target and actual wrist
                if (abs(Wrist.getRadians()-currWristTarget)>TuningConstants.VISION_TRAP_TOL){
                    //Ends previous wrist command and starts new trapezoid one
                    wristCommand.end(true)
                    wristCommand = SetWrist(currWristTarget)
                    wristCommand.schedule()
                    wristIsTrap = true
                }
            } else {
                if (abs(Wrist.getRadians()-currWristTarget)>TuningConstants.VISION_TRAP_TOL){
                    wristCommand = SetWrist(currWristTarget)
                    wristCommand.schedule()
                    wristIsTrap = true
                } else {
                    wristCommand = SetWrist(currWristTarget, false, true)
                }
            }
        }
    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun end(interrupted: Boolean) {

    }
}
