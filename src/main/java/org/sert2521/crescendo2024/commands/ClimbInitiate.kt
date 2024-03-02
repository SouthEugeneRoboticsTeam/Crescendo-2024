package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.PhysicalConstants.CLIMBER_MIN
import org.sert2521.crescendo2024.subsystems.Climber
import kotlin.math.min

class ClimbInitiate : Command() {


    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Climber)
    }

    override fun initialize() {
        Climber.setCurrentLimit(4)
    }

    override fun execute() {
        Climber.setSpeed(-0.7, -0.7)
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        //println(Pair(Climber.getStall(1), Climber.getStall(2)))
        return min(Climber.getEncoder(1), Climber.getEncoder(2)) <= CLIMBER_MIN //|| (Climber.getStall(1) && Climber.getStall(2))
    }

    override fun end(interrupted: Boolean) {
        Climber.setSpeed(0.0, 0.0)
        Climber.setCurrentLimit(26)
    }
}
