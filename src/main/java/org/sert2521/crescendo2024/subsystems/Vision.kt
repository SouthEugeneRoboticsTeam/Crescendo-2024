package org.sert2521.crescendo2024.subsystems

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.photonvision.EstimatedRobotPose
import org.photonvision.PhotonCamera
import org.photonvision.PhotonPoseEstimator
import org.sert2521.crescendo2024.Input
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.TuningConstants
import java.util.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

object Vision : SubsystemBase() {

    //private val cam = PhotonCamera("Left2")


    //private val visionPoseEstimator = PhotonPoseEstimator(PhysicalConstants.usedField, PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, cam, PhysicalConstants.centerPose)
    //private var estimation:Optional<EstimatedRobotPose>

    init{
        //estimation = visionPoseEstimator.update()
    }

    override fun periodic() {
        //estimation = visionPoseEstimator.update()
    }

    fun getEstimation():Optional<EstimatedRobotPose>{
        return Optional.empty()
    }

    fun getPose():Pose2d{
        return Pose2d()
    }

    fun getDistanceSpeaker():Double{
        //var distance:Double

        val speakerTrans = if (Input.getColor()==DriverStation.Alliance.Blue){
            PhysicalConstants.speakerTransBlue
        } else {
            PhysicalConstants.speakerTransRed
        }

        return getPose().translation.getDistance(speakerTrans)
    }

    fun getVisionWristAngle():Double{

        val distance:Double
        if (Input.getColor()==DriverStation.Alliance.Red){
            distance = getDistanceSpeaker()-(abs(getDriveAngleTarget().degrees-180.0)*TuningConstants.VISION_ANGLE_COR)
        } else {
            distance = getDistanceSpeaker()-(abs(getDriveAngleTarget().degrees)*TuningConstants.VISION_ANGLE_COR)
        }
        return TuningConstants.wristAngLookup.get(distance)
    }

    fun getDriveAngleTarget():Rotation2d{
        val estimationPose = Vision.getPose()
        //val speedY = Drivetrain.getAbsoluteSpeeds().vyMetersPerSecond
        val speakerTrans = if (Input.getColor()==DriverStation.Alliance.Red){
            PhysicalConstants.speakerTransRed
        } else {
            PhysicalConstants.speakerTransBlue
        }
        return Rotation2d(PI/2- atan2((estimationPose.x-speakerTrans.x), (estimationPose.y-(speakerTrans.y/*-speedY*TuningConstants.VIS_DRIVE_OFFSET_MULT*/))))
    }




}