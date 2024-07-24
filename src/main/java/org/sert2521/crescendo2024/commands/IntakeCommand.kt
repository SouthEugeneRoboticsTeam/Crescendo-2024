package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.subsystems.Indexer
import org.sert2521.crescendo2024.subsystems.Intake
import org.sert2521.crescendo2024.subsystems.Wrist

class IntakeCommand : Command() {
    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Indexer, Intake)
    }

    override fun initialize() {
        Intake.setMotor(0.9)
        Indexer.setMotor(0.5)
    }

    override fun execute() {
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute(
        return Indexer.getBeamBreak()
    }

    override fun end(interrupted: Boolean) {
        Intake.stop()
        Indexer.stop()
    }
}
