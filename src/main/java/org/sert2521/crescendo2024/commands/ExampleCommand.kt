package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.crescendo2024.subsystems.ExampleSubsystem

/** An example command that uses an example subsystem.  */
class ExampleCommand : CommandBase()
{
    init
    {
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(ExampleSubsystem)
    }

    override fun initialize()
    {
        // Called when the command is initially scheduled.
        // Here we show an example of calling an action on the ExampleSubsystem
        ExampleSubsystem.exampleAction()
    }

    override fun execute()
    {
        // Called every time the scheduler runs while the command is scheduled.
    }

    override fun end(interrupted: Boolean)
    {
        // Called once the command ends or is interrupted.
    }

    override fun isFinished(): Boolean
    {
        // Returns true when the command should end.
        return false
    }
}
