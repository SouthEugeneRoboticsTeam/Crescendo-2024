package org.sert2521.crescendo2024.commands


import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.filter.Debouncer
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Wrist
import kotlin.math.PI
import kotlin.math.abs

class SetWrist(private val goal:Double, private val ends:Boolean) : Command() {


    private var wristAngle = Wrist.getRadians()
    private var feedForward = ArmFeedforward(TuningConstants.WRIST_S, TuningConstants.WRIST_G, TuningConstants.WRIST_V, TuningConstants.WRIST_A)
    //var pid = PIDController(TuningConstants.WRIST_P, TuningConstants.WRIST_I, TuningConstants.WRIST_D)
    private var pid = ProfiledPIDController(TuningConstants.WRIST_P, TuningConstants.WRIST_I, TuningConstants.WRIST_D, TuningConstants.trapConstraints)
    private var atSetpoint = false
    private var filter = Debouncer(0.2)
    private var end = false

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Wrist)
    }

    override fun initialize() {
        wristAngle = Wrist.getRadians()
        pid.reset(wristAngle+2*PI)
    }

    override fun execute() {

        wristAngle = Wrist.getRadians()

        val pidResult =  pid.calculate(wristAngle+2*PI, goal+2*PI)
        val feedforwardResult = feedForward.calculate(wristAngle, 0.0)
        //println(pid.setpoint.velocity)

        Wrist.setVoltage(feedforwardResult + pidResult)
        if(ends){
            atSetpoint = abs(wristAngle - goal) <= TuningConstants.WRIST_ANGLE_TOLERANCE
            end = filter.calculate(atSetpoint)
        }

    }

    override fun isFinished(): Boolean {
        return end
    }

    override fun end(interrupted: Boolean) {
        Wrist.stop()
    }
}
