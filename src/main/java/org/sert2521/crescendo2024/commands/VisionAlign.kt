package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.SwerveConstants
import org.sert2521.crescendo2024.subsystems.Drivetrain
import kotlin.math.*

class VisionAlign(): Command() {

    private val drivePID = PIDController(SwerveConstants.VISION_DRIVE_P, SwerveConstants.VISION_DRIVE_I, SwerveConstants.VISION_DRIVE_D)
    private val anglePID = PIDController(SwerveConstants.VISION_ANGLE_P, SwerveConstants.VISION_ANGLE_I, SwerveConstants.VISION_ANGLE_D)

    private var xTarget = 0.0
    private var yTarget = 0.0

    private var xError = 0.0
    private var yError = 0.0
    private var angleError = 0.0
    private var error = 0.0

    private var angleTarget = 0.0

    private var angle = 0.0
    private var pidResult = 0.0
    private var xResult = 0.0
    private var yResult = 0.0
    private var angleResult = 0.0

    init{ addRequirements(Drivetrain)}

    override fun initialize() {

        xTarget = Drivetrain.getNearestTarget().x
        yTarget = Drivetrain.getNearestTarget().y
        angleTarget = Drivetrain.getNearestTarget().rotation.radians

    }

    override fun execute() {

        xError = xTarget - Drivetrain.getVisionPose().x
        yError = yTarget - Drivetrain.getVisionPose().y
        angleError = angleTarget - Drivetrain.getVisionPose().rotation.radians

        angle = atan2(yError, xError)
        error = sqrt( xError.pow(2) + yError.pow(2) )

        pidResult = drivePID.calculate(error, 0.0)
        angleResult = anglePID.calculate(Drivetrain.getVisionPose().rotation.radians, angleTarget)

        xResult = pidResult * cos(angle)
        yResult = pidResult * sin(angle)

        Drivetrain.drive(ChassisSpeeds(xResult, yResult, angleResult))

    }
}
