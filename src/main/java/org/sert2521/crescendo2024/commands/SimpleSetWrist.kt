package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.subsystems.Wrist

class SimpleSetWrist(private val targetPosition:Double) : Command() {
    private val pidLoop = ProfiledPIDController(1.0, 0.0, 0.0,
        TrapezoidProfile.Constraints(1.0, 2.0))
    private val feedforward = ArmFeedforward(0.3, 2.4, 1.0, 0.2)

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Wrist)
    }

    override fun initialize() {}

    override fun execute() {

        val pidOutput = pidLoop.calculate(Wrist.getRadians(), targetPosition)
        val feedforwardOutput = feedforward.calculate(pidLoop.setpoint.position, pidLoop.setpoint.velocity)

        Wrist.setVoltage(pidOutput+feedforwardOutput)
    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun end(interrupted: Boolean) {}
}
