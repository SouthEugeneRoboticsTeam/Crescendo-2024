package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.Input
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.*
import kotlin.math.*

class VisionAlign() : Command() {

    var prevWristTarget = PhysicalConstants.WRIST_SETPOINT_STOW
    var currWristTarget = PhysicalConstants.WRIST_SETPOINT_STOW
    var drivetrainTarget:Rotation2d? = Rotation2d(0.0)
    var wristCommand:Command = SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW, false)
    var commandedWristAngle = PhysicalConstants.WRIST_SETPOINT_STOW
    //DOES NOT MEAN TRAP AS IN THE STAGE THINGY
    var wristIsTrap = true

    val driveAlignPID = PIDController(TuningConstants.VISION_ALIGN_P, TuningConstants.VISION_ALIGN_I, TuningConstants.VISION_ALIGN_D)

    init {
        addRequirements(Vision)
    }

    override fun initialize() {
        driveAlignPID.enableContinuousInput(-PI, PI)
        driveAlignPID.setTolerance(TuningConstants.VISION_TOLERANCE)
        currWristTarget = Vision.getVisionWristAngle()
        drivetrainTarget = Vision.getDriveAngleTarget()
        currWristTarget=Vision.getVisionWristAngle()
        RuntimeConstants.wristVision = currWristTarget
        wristCommand = SetWrist(currWristTarget)
        wristCommand.schedule()
        wristIsTrap = true
    }

    override fun execute() {
        drivetrainTarget=Vision.getDriveAngleTarget()
        if (drivetrainTarget == null){
            RuntimeConstants.visionAligning = false
            driveAlignPID.reset()
        } else {

            RuntimeConstants.visionAligning = true
            //Maybe square it or smth
            var error = (Vision.getPose().rotation.radians-drivetrainTarget!!.radians-(PI))
            if (error < -2*PI){
                error += 2*PI
            } else if (error > 2*PI){
                error -= 2*PI
            }
            error += PI
            SmartDashboard.putNumber("Error", error)
            if (error.absoluteValue<TuningConstants.VISION_TOLERANCE){
                error = 0.0
            }
            RuntimeConstants.visionRightStick = driveAlignPID.calculate((error.absoluteValue).pow(1.5)*error.sign)+TuningConstants.VISION_ALIGN_S*error.sign

            SmartDashboard.putNumber("Vision Right Stick", RuntimeConstants.visionRightStick)
        }
        if (driveAlignPID.atSetpoint()){
            drivetrainTarget = Vision.getDriveAngleTarget()
            currWristTarget=Vision.getVisionWristAngle()
        }

        currWristTarget=Vision.getVisionWristAngle()
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
            /*
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
                    wristCommand.schedule()
                    wristIsTrap = false
                }
            }
             */
        }





    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun end(interrupted: Boolean) {
        RuntimeConstants.visionRightStick = 0.0
        RuntimeConstants.visionAligning = false
    }



}
