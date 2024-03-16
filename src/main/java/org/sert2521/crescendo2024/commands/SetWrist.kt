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

class SetWrist(private val goal:Double, private val ends:Boolean = true, private val useVision:Boolean = false) : Command() {


    private var wristAngle = Wrist.getRadians()
    private var feedForward = ArmFeedforward(TuningConstants.WRIST_S, TuningConstants.WRIST_G, TuningConstants.WRIST_V, TuningConstants.WRIST_A)
    //var pid = PIDController(TuningConstants.WRIST_P, TuningConstants.WRIST_I, TuningConstants.WRIST_D)
    private var pid = ProfiledPIDController(TuningConstants.WRIST_P, TuningConstants.WRIST_I, TuningConstants.WRIST_D, TuningConstants.trapConstraints)
    private var notProfiled = PIDController(TuningConstants.WRIST_P, TuningConstants.WRIST_I, TuningConstants.WRIST_D)

    private var done = false

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Wrist)
    }

    override fun initialize() {
        wristAngle = Wrist.getRadians()
        pid.reset(wristAngle+2*PI)
        RuntimeConstants.wristSetPoint=goal
    }

    override fun execute() {
        done = false
        val pidResult:Double
        wristAngle = Wrist.getRadians()
        if (ends){
            pidResult =  pid.calculate(wristAngle+2*PI, goal+2*PI)
        } else {
            if (useVision){
                pidResult = notProfiled.calculate(wristAngle+2*PI, RuntimeConstants.wristVision+2*PI)
            } else {
                pidResult = notProfiled.calculate(wristAngle + 2 * PI, goal + 2 * PI)
            }
        }
        val feedforwardResult = feedForward.calculate(wristAngle, pid.setpoint.velocity)
        //println(pid.setpoint.velocity)

        Wrist.setVoltage(feedforwardResult + pidResult)

        if (ends && Wrist.getRadians()>goal-TuningConstants.WRIST_ANGLE_TOLERANCE && Wrist.getRadians()<goal+TuningConstants.WRIST_ANGLE_TOLERANCE){
            done=true
        }
    }

    override fun isFinished(): Boolean {
        return done
    }

    override fun end(interrupted: Boolean) {
        if (interrupted){
            RuntimeConstants.wristSetPoint
        }
    }
}
