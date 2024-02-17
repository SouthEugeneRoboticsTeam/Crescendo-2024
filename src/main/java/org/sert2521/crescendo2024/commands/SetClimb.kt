package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.PhysicalConstants.CLIMBER_MAX
import org.sert2521.crescendo2024.PhysicalConstants.CLIMBER_MIN
import org.sert2521.crescendo2024.TuningConstants.CLIMBER_SPEED
import org.sert2521.crescendo2024.subsystems.Climber

class SetClimb : Command() {

    var up = true
    var speed = -0.5
    var done = false

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Climber)
    }

    override fun initialize() {}

    override fun execute() {
        if(up){
            speed = CLIMBER_SPEED
        }else{
            speed = -CLIMBER_SPEED
        }
        Climber.setSpeed(speed, speed)

        if (up && (Climber.getEncoder(1) > CLIMBER_MAX)) {
            up = false
            speed = -CLIMBER_SPEED
        }
        if (!up && (Climber.getEncoder(1) < CLIMBER_MIN)) {
            done = true
        }
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute() }
        return done
    }

    override fun end(interrupted: Boolean) {
        Climber.setSpeed(0.0, 0.0)
    }
}
