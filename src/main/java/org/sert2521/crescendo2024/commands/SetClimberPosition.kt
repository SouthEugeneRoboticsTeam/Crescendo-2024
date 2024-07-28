package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.subsystems.Climber

class SetClimberPosition : Command() {

    private var time = 0.0
    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Climber)
    }

    override fun initialize() {
        time = 0.0
        Climber.setSpeed(1.0, 1.0)
    }

    override fun execute() {
        time += 0.02
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return time >= 3.0
    }

    override fun end(interrupted: Boolean) {
        Climber.stop()
    }
}
