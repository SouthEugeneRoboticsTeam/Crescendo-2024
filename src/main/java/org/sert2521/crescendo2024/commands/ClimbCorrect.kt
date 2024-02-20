package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.filter.Debouncer
import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Climber
import org.sert2521.crescendo2024.subsystems.Drivetrain
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlin.math.tan

class ClimbCorrect(val startEstimateOne:Double, val startEstimateTwo: Double) : Command() {

    //One is the a/x climber
    //Two is the b/y climber


    private var estimateOne = startEstimateOne
    private var estimateTwo = startEstimateTwo

    private var encoderOne = {Climber.getEncoder(1)}
    private var encoderTwo = {Climber.getEncoder(2)}

    private var prevEncoderOne = 0.0
    private var prevEncoderTwo = 0.0

    private var angle = 0.0
    private var prevAngle = 0.0
    private var estimateAngle = 0.0

    //filter represents whether the climber's estimate is correct
    private val filter = Debouncer(TuningConstants.CLIMBER_FILTER)

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Climber)
    }

    override fun initialize() {
        angle = Drivetrain.getRoll()
        estimateAngle = Drivetrain.getRoll()
    }

    override fun execute() {
        val filterResult = filter.calculate(angle.absoluteValue > TuningConstants.CLIMBER_TOLERANCE_ANGLE && (prevAngle-angle).absoluteValue < TuningConstants.CLIMBER_RESTING_TOLERANCE || !atSetpoint())

        if (!filterResult && atSetpoint()){

            //Generated by totally secure and airtight math
            //Just trust me bro
            if (encoderOne()>encoderTwo()){
                estimateOne = encoderOne()*sin(PI/2-angle)
                estimateTwo = sin(PI/2-angle)*(encoderTwo()+PhysicalConstants.CLIMBER_BETWEEN_DISTANCE/tan(PI/2-angle))
            } else {
                estimateTwo = encoderTwo()*sin(PI/2-angle)
                estimateOne = sin(PI/2-angle)*(encoderOne()+PhysicalConstants.CLIMBER_BETWEEN_DISTANCE/tan(PI/2-angle))
            }
        }
        //AAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHH
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return false
    }

    override fun end(interrupted: Boolean) {}

    fun atSetpoint():Boolean{
        return (encoderOne()-estimateOne).absoluteValue <= TuningConstants.CLIMBER_TOLERANCE_ENCODER && (encoderTwo()-estimateTwo).absoluteValue <= TuningConstants.CLIMBER_TOLERANCE_ENCODER
    }
}