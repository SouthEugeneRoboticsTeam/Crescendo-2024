package org.sert2521.crescendo2024.commands


import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Wrist
import kotlin.math.PI
import kotlin.math.abs

class SetWrist(private val goal:Double, private val ends:Boolean) : Command() {


    private var wristAngle = Wrist.getRadians()
    private var feedForward = ArmFeedforward(TuningConstants.WRIST_S, TuningConstants.WRIST_G, TuningConstants.WRIST_V, TuningConstants.WRIST_A)
    private var pid = ProfiledPIDController(TuningConstants.WRIST_P, TuningConstants.WRIST_I, TuningConstants.WRIST_D, TuningConstants.trapConstraints)
    private var atSetpoint = false
    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Wrist)
    }

    override fun initialize() {}

    override fun execute() {
        wristAngle = Wrist.getRadians()
        Wrist.setVoltage(feedForward.calculate(wristAngle, 0.0) + pid.calculate(wristAngle+2*PI, RuntimeConstants.wristSetPoint+2*PI))
        if(ends){
            atSetpoint = abs(wristAngle - goal) <= TuningConstants.WRIST_ANGLE_TOLERANCE
        }
    }

    override fun isFinished(): Boolean {
        return atSetpoint
    }

    override fun end(interrupted: Boolean) {
        Wrist.stop()
    }
}
