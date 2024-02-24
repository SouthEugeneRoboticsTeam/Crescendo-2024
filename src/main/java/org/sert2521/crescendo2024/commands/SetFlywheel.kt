package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.ConfigConstants
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Flywheel
import java.io.ObjectInputFilter.Config

class SetFlywheel(private val rpm:Double, private val ends: Boolean = false) : Command() {
    private val pid = PIDController(TuningConstants.FLYWHEEL_P, TuningConstants.FLYWHEEL_I, TuningConstants.FLYWHEEL_D)
    private val feedForward = SimpleMotorFeedforward(TuningConstants.FLYWHEEL_KS, TuningConstants.FLYWHEEL_KV, TuningConstants.FLYWHEEL_KA)

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Flywheel)
    }

    override fun initialize() {
        RuntimeConstants.flywheelGoal=rpm
    }

    override fun execute() {
        Flywheel.setVoltage(pid.calculate(Flywheel.getSpeed(), rpm) + feedForward.calculate(rpm))
        RuntimeConstants.flywheelRevved =  Flywheel.getSpeed() > ConfigConstants.FLYWHEEL_SHOOT_SPEED
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return RuntimeConstants.flywheelRevved && ends
    }
//36:24
    override fun end(interrupted: Boolean) {
        if (interrupted){
            RuntimeConstants.flywheelGoal = 0.0
        }
        Flywheel.setVoltage(0.0)
        RuntimeConstants.flywheelRevved = false
    }
}
