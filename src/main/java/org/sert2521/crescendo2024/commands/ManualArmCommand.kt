package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Wrist

class ManualArmCommand(val speed:Double) : Command() {
    private val wrist = Wrist

    private var feedForward = ArmFeedforward(TuningConstants.WRIST_S, TuningConstants.WRIST_G, TuningConstants.WRIST_V, TuningConstants.WRIST_A)

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(wrist)
    }

    override fun initialize() {}

    override fun execute() {
        Wrist.setVoltage(feedForward.calculate(Wrist.getRadians(), 0.0)+speed)
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return false
    }

    override fun end(interrupted: Boolean) {
        RuntimeConstants.wristSetPoint=Wrist.getRadians()
    }
}
