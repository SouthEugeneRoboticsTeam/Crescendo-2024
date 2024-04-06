package org.sert2521.crescendo2024.subsystems

import com.fasterxml.jackson.databind.util.Named
import edu.wpi.first.wpilibj2.command.SubsystemBase
import com.pathplanner.lib.auto.AutoBuilder
import com.pathplanner.lib.auto.NamedCommands
import com.pathplanner.lib.commands.PathPlannerAuto
import com.pathplanner.lib.path.PathPlannerPath
import com.pathplanner.lib.util.HolonomicPathFollowerConfig
import com.pathplanner.lib.util.PIDConstants
import com.pathplanner.lib.util.ReplanningConfig
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.FunctionalCommand
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.WaitCommand

import org.sert2521.crescendo2024.ConfigConstants
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.SwerveConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.commands.*
import kotlin.io.path.Path


object Autos : SubsystemBase() {
    private var autoChooser = SendableChooser<Command>()
    private var extendedChooser = SendableChooser<Command>()
    val defaultAutoCommand = Commands.none()

    var whee = false

    var commandList = mapOf<String, Command>(
            "Outtake" to Outtake(true).withTimeout(0.4),
            "Intake Note" to IntakeCommand().withTimeout(2.0),
            "Wrist Stow" to SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).asProxy(),
            "Wrist Podium" to SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM).asProxy(),
            "Wrist Podium Minus" to SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_MINUS).asProxy(),
            "Wrist Podium Plus" to SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_PLUS).asProxy(),
            "Wrist Podium Double Plus" to SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_DOUBLE_PLUS).asProxy(),
            "Wrist Podium Triple Plus" to SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_TRIPLE_PLUS).asProxy(),
            "Wrist Podium Plus Half" to SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_PLUS_HALF).asProxy(),
            "Wrist Far" to SetWrist(PhysicalConstants.WRIST_SETPOINT_FAR).asProxy(),
            "Wrist Vision" to StartVision().andThen(WristVision()),
            "Wrist Vision Debounce" to StartVision().andThen(WristVision(true)),
            "Finish Vision" to CancelVision(),
            "Flywheel Rev" to SetFlywheel(ConfigConstants.FLYWHEEL_SHOOT_SPEED, true).asProxy(),
            "Flywheel Stop" to SetFlywheel(ConfigConstants.FLYWHEEL_IDLE_SPEED, true).asProxy(),
            "Podium Shot" to IntakeCommand().andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM).asProxy()).andThen(Outtake().withTimeout(0.3)).andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).asProxy()),
            "Podium Minus Shot" to IntakeCommand().andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_MINUS).asProxy()).andThen(Outtake().withTimeout(0.3)).andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).asProxy()),
            "Podium Plus Shot" to IntakeCommand().andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_PLUS).asProxy()).andThen(Outtake().withTimeout(0.3)).andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).asProxy()),
            "Podium Double Plus Shot" to IntakeCommand().andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_PODIUM_DOUBLE_PLUS).asProxy()).andThen(Outtake().withTimeout(0.3)).andThen(SetWrist(PhysicalConstants.WRIST_SETPOINT_STOW).asProxy())
    )


    init {
        AutoBuilder.configureHolonomic(
            Drivetrain::getPose,
            Drivetrain::setNewPose,
            Drivetrain::getReletiveSpeeds,
            {Drivetrain.drive(ChassisSpeeds(it.vxMetersPerSecond, it.vyMetersPerSecond, it.omegaRadiansPerSecond))},
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
        autoChooser.addOption("6 Piece", PathPlannerAuto("6 Piece 60 Amps"))
        autoChooser.addOption("6 Piece Vision", PathPlannerAuto("6 Piece Vision"))
        autoChooser.addOption("5 Piece Amp Race", PathPlannerAuto("5 Piece Amp 3 Far"))
        autoChooser.addOption("5 Piece Amp Race Vision", PathPlannerAuto("5 Piece Amp 3 Far Vision"))
        //autoChooser.addOption("6 Piece Original", PathPlannerAuto("6 Piece Center 2 Far"))
        //autoChooser.addOption("6 Piece New Order", PathPlannerAuto("6 Piece New Order"))
        autoChooser.addOption("4 Piece Source", PathPlannerAuto("4 Piece Source 3 Far"))
        autoChooser.addOption("4 Piece Source", PathPlannerAuto("4 Race"))
        autoChooser.addOption("4 Piece Source Vision", PathPlannerAuto("4 Piece Source Vision"))
        //autoChooser.addOption("Amp Race 3rd Mid First", PathPlannerAuto("5 Amp Race 3rd First"))
        autoChooser.setDefaultOption("None", Commands.none())
        //NOT FINISHED
        /*
        autoChooser.addOption("4 Piece Source No Stop",
                (WaitCommand(0.5).andThen(AutoBuilder.followPath(PathPlannerPath.fromChoreoTrajectory("4 Source.1.traj"))))
                        .alongWith(SetFlywheel(ConfigConstants.FLYWHEEL_SHOOT_SPEED))
                        .deadlineWith(WaitCommand(0.5).andThen(Outtake().withTimeout(0.4)).andThen(WaitCommand(0.5)).andThen(IntakeCommand()))
        )
         */

        /*
        autoChooser.addOption("6 Piece w Logic",
                (PathPlannerAuto("6 Piece First 4.5"))
                .andThen(
                        (PathPlannerAuto("6 Piece 5th Normal").onlyIf{ Indexer.getBeamBreak() })
                        .alongWith(PathPlannerAuto("6 Piece 5th Failed").onlyIf{ !Indexer.getBeamBreak() })
                ).andThen(
                        (PathPlannerAuto("6 Piece 6th Normal").onlyIf{ Indexer.getBeamBreak() })
                        .alongWith(PathPlannerAuto("6 Piece 6th Failed").onlyIf{ !Indexer.getBeamBreak() })
                )
        )

        autoChooser.addOption("5 Piece Amp Race w Logic",
                (PathPlannerAuto("5 Piece Amp Race First 2.5"))
                .andThen(
                        (PathPlannerAuto("5 Piece Amp Race 3rd Normal").onlyIf{ Indexer.getBeamBreak() })
                        .alongWith(PathPlannerAuto("5 Piece Amp Race 3rd Failed").onlyIf{ !Indexer.getBeamBreak() })
                ).andThen(
                        (PathPlannerAuto("5 Piece Amp Race 4th Normal").onlyIf{ Indexer.getBeamBreak() })
                        .alongWith(PathPlannerAuto("5 Piece Amp Race 4th Failed").onlyIf{ !Indexer.getBeamBreak() })
                ).andThen(
                        (PathPlannerAuto("5 Piece Amp Race 5th Normal").onlyIf{ Indexer.getBeamBreak() })
                        .alongWith(PathPlannerAuto("5 Piece Amp Race 5th Failed").onlyIf{ !Indexer.getBeamBreak() })
                )
        )

        autoChooser.addOption("4 Source 3 Mid w Logic",
                (PathPlannerAuto("4 Source First 1.5"))
                .andThen(
                        (PathPlannerAuto("4 Source 2nd Normal").onlyIf{ Indexer.getBeamBreak() })
                        .alongWith(PathPlannerAuto("4 Source 2nd Failed").onlyIf{ !Indexer.getBeamBreak() })
                ).andThen(
                        (PathPlannerAuto("4 Source 3rd Normal").onlyIf{ Indexer.getBeamBreak() })
                        .alongWith(PathPlannerAuto("4 Source 3rd Failed").onlyIf{ !Indexer.getBeamBreak() })
                ).andThen(
                        (PathPlannerAuto("4 Source 4th Normal").onlyIf{ Indexer.getBeamBreak() })
                        .alongWith(PathPlannerAuto("4 Source 4th Failed").onlyIf{ !Indexer.getBeamBreak() })
                )
          )
         */

        //extendedChooser = AutoBuilder.buildAutoChooser()


        //SmartDashboard.putData("Extended Auto Chooser", extendedChooser)
        SmartDashboard.putData("Auto Chooser", autoChooser)
    }

    fun getAuto(): Command{
        return autoChooser.selected
    }
}