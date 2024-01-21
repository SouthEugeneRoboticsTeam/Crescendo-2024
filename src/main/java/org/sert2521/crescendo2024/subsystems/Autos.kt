package org.sert2521.crescendo2024.subsystems

import edu.wpi.first.wpilibj2.command.SubsystemBase
import com.pathplanner.lib.auto.AutoBuilder
import com.pathplanner.lib.util.HolonomicPathFollowerConfig
import com.pathplanner.lib.util.PIDConstants
import com.pathplanner.lib.util.ReplanningConfig
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.SwerveConstants


object Autos : SubsystemBase() {
    val autoChooser = AutoBuilder.buildAutoChooser()
    val defaultAutoCommand = null


    init {
        AutoBuilder.configureHolonomic(
            Drivetrain::getPose,
            Drivetrain::setNewPose,
            Drivetrain::getReletiveSpeeds,
            Drivetrain::drive,
            HolonomicPathFollowerConfig(PIDConstants(SwerveConstants.AUTO_POWER_P, SwerveConstants.AUTO_POWER_I, SwerveConstants.AUTO_POWER_D),
                PIDConstants(SwerveConstants.AUTO_ANGLE_P, SwerveConstants.AUTO_ANGLE_I, SwerveConstants.AUTO_ANGLE_D),
                SwerveConstants.MAX_AUTO_SPEED,
                SwerveConstants.DRIVE_BASE_RADIUS,
                ReplanningConfig(false, true, SwerveConstants.AUTO_REPLANNING_TOTAL_ERROR, SwerveConstants.AUTO_REPLANNING_SPIKE)
            ),
            {false},
            Drivetrain
        )

        SmartDashboard.putData("Auto Chooser", autoChooser)
    }

    fun getAuto(): Command?{
        return autoChooser.selected
    }
}