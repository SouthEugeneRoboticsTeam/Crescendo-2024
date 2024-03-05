package org.sert2521.crescendo2024.subsystems

import com.fasterxml.jackson.databind.util.Named
import edu.wpi.first.wpilibj2.command.SubsystemBase
import com.pathplanner.lib.auto.AutoBuilder
import com.pathplanner.lib.auto.NamedCommands
import com.pathplanner.lib.util.HolonomicPathFollowerConfig
import com.pathplanner.lib.util.PIDConstants
import com.pathplanner.lib.util.ReplanningConfig
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands

import org.sert2521.crescendo2024.ConfigConstants
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.SwerveConstants
import org.sert2521.crescendo2024.commands.IntakeCommand
import org.sert2521.crescendo2024.commands.Outtake
import org.sert2521.crescendo2024.commands.SetFlywheel
import org.sert2521.crescendo2024.commands.SetWrist


object Autos : SubsystemBase() {
    private var autoChooser: SendableChooser<Command>
    val defaultAutoCommand = Commands.none()



    var commandList = mapOf<String, Command>(
            "Outtake" to Outtake(true).withTimeout(0.4),
            "Intake Note" to IntakeCommand(),
            "Wrist Stow" to SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).asProxy(),
            "Wrist Podium" to SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM).asProxy(),
            "Wrist Podium Minus" to SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_MINUS).asProxy(),
            "Wrist Podium Plus" to SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_PLUS).asProxy(),
            "Wrist Podium Double Plus" to SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_DOUBLE_PLUS).asProxy(),
            "Wrist Far" to SetWrist(PhysicalConstants.WRIST_SETPOINT_FAR).asProxy(),
            "Flywheel Rev" to SetFlywheel(ConfigConstants.FLYWHEEL_SHOOT_SPEED, true).asProxy(),
            "Flywheel Stop" to SetFlywheel(ConfigConstants.FLYWHEEL_IDLE_SPEED, true).asProxy(),
            "Podium Shot" to IntakeCommand().andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM).asProxy()).andThen(Outtake().withTimeout(0.2)).andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).asProxy()),
            "Podium Minus Shot" to IntakeCommand().andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_MINUS).asProxy()).andThen(Outtake().withTimeout(0.2)).andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).asProxy()),
            "Podium Plus Shot" to IntakeCommand().andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_PLUS).asProxy()).andThen(Outtake().withTimeout(0.2)).andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).asProxy()),
            "Podium Double Plus Shot" to IntakeCommand().andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_DOUBLE_PLUS).asProxy()).andThen(Outtake().withTimeout(0.2)).andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).asProxy())
    )


    init {
        AutoBuilder.configureHolonomic(
            Drivetrain::getPose,
            Drivetrain::setNewPose,
            Drivetrain::getReletiveSpeeds,
            {Drivetrain.drive(ChassisSpeeds(-it.vxMetersPerSecond, -it.vyMetersPerSecond, -it.omegaRadiansPerSecond))},
            HolonomicPathFollowerConfig(PIDConstants(SwerveConstants.AUTO_POWER_P, SwerveConstants.AUTO_POWER_I, SwerveConstants.AUTO_POWER_D),
                PIDConstants(SwerveConstants.AUTO_ANGLE_P, SwerveConstants.AUTO_ANGLE_I, SwerveConstants.AUTO_ANGLE_D),
                SwerveConstants.MAX_AUTO_SPEED,
                SwerveConstants.DRIVE_BASE_RADIUS,
                ReplanningConfig(false, true, SwerveConstants.AUTO_REPLANNING_TOTAL_ERROR, SwerveConstants.AUTO_REPLANNING_SPIKE)
            ),
            {DriverStation.getAlliance().get()==DriverStation.Alliance.Red},
            Drivetrain
        )


        NamedCommands.registerCommands(commandList)

        autoChooser = AutoBuilder.buildAutoChooser()

        SmartDashboard.putData("Auto Chooser", autoChooser)
    }

    fun getAuto(): Command?{
        return if (autoChooser.selected == null){
            defaultAutoCommand
        } else {
            autoChooser.selected
        }
    }
}