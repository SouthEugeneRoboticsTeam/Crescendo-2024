package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.ConfigConstants
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Flywheel
import java.io.ObjectInputFilter.Config

class SetFlywheel(private val rpm:Double) : Command() {
    val pid = PIDController(TuningConstants.FLYWHEEL_P, TuningConstants.FLYWHEEL_I, TuningConstants.FLYWHEEL_D)
    val feedForward = SimpleMotorFeedforward(TuningConstants.FLYWHEEL_KS, TuningConstants.FLYWHEEL_KV, TuningConstants.FLYWHEEL_KA)

    init {
        println("ran rev")
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Flywheel)
    }

    override fun initialize() {}

    override fun execute() {
        println("set rev")
        println(-pid.calculate(rpm, Flywheel.getSpeed())+ feedForward.calculate(rpm))
        Flywheel.setVoltage(-pid.calculate(rpm, Flywheel.getSpeed()) + feedForward.calculate(rpm))
        RuntimeConstants.flywheelRevved =  Flywheel.getSpeed() > ConfigConstants.FLYWHEEL_SHOOT_SPEED-10.0
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return false
    }
//36:24
    override fun end(interrupted: Boolean) {
        SetFlywheel(ConfigConstants.FLYWHEEL_IDLE_SPEED)
    }
}
