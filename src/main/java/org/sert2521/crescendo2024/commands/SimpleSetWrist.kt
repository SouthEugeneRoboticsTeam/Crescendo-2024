package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Wrist
import kotlin.math.PI

class SimpleSetWrist(private val targetPosition:Double) : Command() {
    private var pidLoop = ProfiledPIDController(TuningConstants.WRIST_P, TuningConstants.WRIST_I, TuningConstants.WRIST_D, TuningConstants.trapConstraints)

    private var feedforward = ArmFeedforward(TuningConstants.WRIST_S, TuningConstants.WRIST_G, TuningConstants.WRIST_V, TuningConstants.WRIST_A)

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Wrist)
    }

    override fun initialize() {
        pidLoop.reset(Wrist.getRadians()+2* PI)
    }

    override fun execute() {

        val pidOutput = pidLoop.calculate(Wrist.getRadians()+2*PI, targetPosition+2*PI)
        val feedforwardOutput = feedforward.calculate(Wrist.getRadians(), pidLoop.setpoint.velocity)

        Wrist.setVoltage(pidOutput+feedforwardOutput)
    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun end(interrupted: Boolean) {}
}
