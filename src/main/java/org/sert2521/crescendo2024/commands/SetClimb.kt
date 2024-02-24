package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.PhysicalConstants.CLIMBER_MAX
import org.sert2521.crescendo2024.PhysicalConstants.CLIMBER_MIN
import org.sert2521.crescendo2024.TuningConstants.CLIMB_SPEED
import org.sert2521.crescendo2024.subsystems.Climber
import org.sert2521.crescendo2024.subsystems.Drivetrain

class SetClimb(private val climbSpeed:Double) : Command() {

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Climber)
    }

    override fun initialize() {
        Drivetrain.enterClimbPos()
    }

    override fun execute() {
        Climber.setSpeed(climbSpeed, climbSpeed)

    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute() }
        return false
    }

    override fun end(interrupted: Boolean) {
        Climber.setSpeed(0.0, 0.0)
    }
}
