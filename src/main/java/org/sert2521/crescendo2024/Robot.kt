package org.sert2521.crescendo2024

import com.revrobotics.spark.config.SparkMaxConfig
import edu.wpi.first.cameraserver.CameraServer
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.InstantCommand
import org.sert2521.crescendo2024.commands.SetFlywheel
import org.sert2521.crescendo2024.subsystems.*
import org.sert2521.crescendo2024.subsystems.Drivetrain
import kotlin.math.PI


/**
 * The VM is configured to automatically run this object (which basically functions as a singleton class),
 * and to call the functions corresponding to each mode, as described in the TimedRobot documentation.
 * This is written as an object rather than a class since there should only ever be a single instance, and
 * it cannot take any constructor arguments. This makes it a natural fit to be an object in Kotlin.
 *
 * If you change the name of this object or its package after creating this project, you must also update
 * the `Main.kt` file in the project. (If you use the IDE's Rename or Move refactorings when renaming the
 * object or package, it will get changed everywhere.)
 */
object Robot : TimedRobot()
{
    /**
     * The autonomous command to run. While a default value is set here,
     * the [autonomousInit] method will set it to the value selected in
     *the  AutoChooser on the dashboard.
     */



    /**
     * This method is run when the robot is first started up and should be used for any
     * initialization code.
     */
    override fun robotInit()
    {
        Input
        Output
        Drivetrain
        CommandScheduler.getInstance().setPeriod(1.0)
    }

    /**
     * This method is called every 20 ms, no matter the mode. Use this for items like
     * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
     *
     * This runs after the mode specific periodic methods, but before LiveWindow and
     * SmartDashboard integrated updating.
     */
    override fun robotPeriodic()
    {
        CommandScheduler.getInstance().run()
        Output.update()
    }

    override fun disabledExit() {
    }

    /** This method is called once each time the robot enters Disabled mode.  */
    override fun disabledInit()
    {

    }

    override fun disabledPeriodic()
    {

    }

    /** This autonomous runs the autonomous command selected by your [RobotContainer] class.  */
    override fun autonomousInit() {}

    /** This method is called periodically during autonomous.  */
    override fun autonomousPeriodic() {}

    override fun autonomousExit() {}

    override fun teleopInit()
    {
        Flywheel.defaultCommand = SetFlywheel(ConfigConstants.FLYWHEEL_IDLE_SPEED)
        RuntimeConstants.wristSetPoint = PhysicalConstants.WRIST_SETPOINT_STOW
    }

    override fun testInit()
    {
        // Cancels all running commands at the start of test mode.
        CommandScheduler.getInstance().cancelAll()
    }
}