package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.PhysicalConstants.CLIMBER_MAX
import org.sert2521.crescendo2024.subsystems.Climber

class SetClimb : Command() {

    var up = true
    var speed = -0.5

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements()
    }

    override fun initialize() {}

    override fun execute() {
        if(up){
            speed = 0.5
        }else{
            speed = -0.5
        }
        Climber.setSpeed(speed, speed)

        if ((up = true) && (Climber.getEncoder(1) == CLIMBER_MAX)) {
            speed = 0.0
        }
        if ((up = false) && (Climber.getEncoder(1) == CLIMBER_MAX)) {
            speed = 0.0
        }
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute() }
        if () {
        }
    }

    override fun end(interrupted: Boolean) {


        }
}
