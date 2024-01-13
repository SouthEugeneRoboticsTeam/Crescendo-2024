package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Wrist

class SetWristCommand : Command() {

    val trapezoidMotionProfile = TrapezoidProfile(TuningConstants.trapConstraints)
    val previousProfiledReference = TrapezoidProfile.State(Wrist.getRadians(), 0.0)
    val time = Timer.getFPGATimestamp()
    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements()
    }

    override fun initialize() {}

    override fun execute() {
        //previousProfiledReference = trapezoidMotionProfile.calculate(Timer.getFPGATimestamp()-time, previousProfiledReference, )
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return false
    }

    override fun end(interrupted: Boolean) {}
}
