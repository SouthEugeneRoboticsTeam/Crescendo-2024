package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.controller.BangBangController
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.*
import org.sert2521.crescendo2024.subsystems.Flywheel
import java.io.ObjectInputFilter.Config
import kotlin.math.min

class SetFlywheel(private var rpm: Double) : Command() {
    private val pidOne = PIDController(TuningConstants.FLYWHEEL_P, TuningConstants.FLYWHEEL_I, TuningConstants.FLYWHEEL_D)
    private val pidTwo = PIDController(TuningConstants.FLYWHEEL_P_COR, TuningConstants.FLYWHEEL_I_COR, TuningConstants.FLYWHEEL_D_COR)

    private val bbOne = BangBangController(5.0)
    private val bbTwo = BangBangController(5.0)

    private val feedForward = SimpleMotorFeedforward(TuningConstants.FLYWHEEL_KS, TuningConstants.FLYWHEEL_KV, TuningConstants.FLYWHEEL_KA)

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Flywheel)
    }

    override fun initialize() {
        RuntimeConstants.flywheelGoal=rpm
    }

    override fun execute() {
        Flywheel.setVoltages(Pair(
            pidOne.calculate(Flywheel.getSpeeds().first, Input.getRightTrigger(0.1) * rpm) + feedForward.calculate(Input.getRightTrigger(0.1) * rpm),
            pidTwo.calculate(Flywheel.getSpeeds().second, Input.getRightTrigger(0.1) * rpm + TuningConstants.FLYWHEEL_OFFSET) + feedForward.calculate(Input.getRightTrigger(0.1) * rpm + TuningConstants.FLYWHEEL_OFFSET)))
        RuntimeConstants.flywheelRevved = min(Flywheel.getSpeeds().first, Flywheel.getSpeeds().second) > Input.getRightTrigger(0.1) * rpm
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return false
    }
//36:24
    override fun end(interrupted: Boolean) {
        if (interrupted){
            RuntimeConstants.flywheelGoal = 0.0
        }
        Flywheel.setVoltages(Pair(0.0, 0.0))
        RuntimeConstants.flywheelRevved = false
        //SetFlywheel(ConfigConstants.FLYWHEEL_IDLE_SPEED)
    }
}
