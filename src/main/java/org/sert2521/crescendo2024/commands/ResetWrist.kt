package org.sert2521.crescendo2024.commands


import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.filter.Debouncer
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.ConfigConstants
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Wrist
import kotlin.math.PI
import kotlin.math.abs

class ResetWrist() : Command() {
    private var wristAngle = Wrist.getRadians()
    private var feedForward = ArmFeedforward(TuningConstants.WRIST_S, TuningConstants.WRIST_G, TuningConstants.WRIST_V, TuningConstants.WRIST_A)

    private var done = false

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Wrist)
    }

    override fun initialize() {
        wristAngle = Wrist.getRadians()
    }

    override fun execute() {
        val feedforwardResult = feedForward.calculate(wristAngle, 0.0)
        //println(pid.setpoint.velocity)

        Wrist.setVoltage(feedforwardResult-0.2)
    }

    override fun isFinished(): Boolean {
        return done
    }

    override fun end(interrupted: Boolean) {
        Wrist.rezeroEncoder()
        PhysicalConstants.WRIST_ENCODER_OFFSET = -0.197
    }
}
