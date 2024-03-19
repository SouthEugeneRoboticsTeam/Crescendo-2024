package org.sert2521.crescendo2024.subsystems

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.photonvision.EstimatedRobotPose
import org.photonvision.PhotonCamera
import org.photonvision.PhotonPoseEstimator
import org.photonvision.targeting.PhotonPipelineResult
import org.photonvision.targeting.PhotonTrackedTarget
import org.sert2521.crescendo2024.Input
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.TuningConstants
import java.util.*
import kotlin.math.tan

object Vision : SubsystemBase() {
    private val cam = PhotonCamera("Left2")
    private var result: PhotonPipelineResult = cam.latestResult

    private var targets:List<PhotonTrackedTarget> = result.targets

    private var bestTarget:PhotonTrackedTarget? = null

    private val visionPoseEstimator = PhotonPoseEstimator(PhysicalConstants.usedField, PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, cam, PhysicalConstants.centerPose)
    private var estimation:Optional<EstimatedRobotPose>

    init{
        estimation = visionPoseEstimator.update()
    }

    override fun periodic() {
        estimation = visionPoseEstimator.update()
    }

    fun getEstimation():Optional<EstimatedRobotPose>{
        return estimation
    }

    fun getPose():Pose2d{
        if (getEstimation().isEmpty){
            return Pose2d(0.0, 0.0, Rotation2d(0.0))
        } else {
            return getEstimation().get().estimatedPose.toPose2d()
        }
    }

    fun getDistanceSpeaker():Double?{
        var distance:Double
        var estimationPose:Pose2d
        var speakerTrans = if (Input.getColor()==DriverStation.Alliance.Blue){
            PhysicalConstants.speakerTransBlue
        } else {
            PhysicalConstants.speakerTransRed
        }
        if (estimation.isEmpty){
            return null
        } else {
            estimationPose = estimation.get().estimatedPose.toPose2d()
        }
        return estimationPose.translation.getDistance(speakerTrans)
    }

    fun getVisionWristAngle():Double{
        val distance = getDistanceSpeaker()
        return if (distance == null){
            PhysicalConstants.WRIST_SETPOINT_STOW
        } else {
            TuningConstants.wristAngLookup.get(distance)
        }
    }

    fun getDriveAngleTarget():Rotation2d?{
        val estimationPose:Pose2d
        val speakerTrans = if (Input.getColor()==DriverStation.Alliance.Red){
            PhysicalConstants.speakerTransRed
        } else {
            PhysicalConstants.speakerTransBlue
        }
        if (estimation.isEmpty){
            return null
        } else {
            estimationPose = estimation.get().estimatedPose.toPose2d()
        }
        return Rotation2d(tan((estimationPose.x-speakerTrans.x)/(estimationPose.y-speakerTrans.y)))
    }
}