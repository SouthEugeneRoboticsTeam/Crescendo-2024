package org.sert2521.crescendo2024.subsystems

import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import edu.wpi.first.math.filter.Debouncer
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.sert2521.crescendo2024.ElectronicIDs
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.TuningConstants

object Climber : SubsystemBase() {
    private val climberMotorOne = SparkMax(ElectronicIDs.CLIMBER_MOTOR_ONE, SparkLowLevel.MotorType.kBrushless)
    private val climberMotorTwo = SparkMax(ElectronicIDs.CLIMBER_MOTOR_TWO, SparkLowLevel.MotorType.kBrushless)

    private val motorOneConfig = SparkMaxConfig()
    private val motorTwoConfig = SparkMaxConfig()

    private val filterOne = Debouncer(TuningConstants.CLIMBER_STALL_TOLERANCE)
    private val filterTwo = Debouncer(TuningConstants.CLIMBER_STALL_TOLERANCE)

    private var currentSpeedOne = 0.0
    private var currentSpeedTwo = 0.0

    private var stallingOne = false
    private var stallingTwo = false
    init {
        motorOneConfig.encoder.positionConversionFactor(PhysicalConstants.CLIMBER_ENCODER_TO_METERS)
        motorTwoConfig.encoder.positionConversionFactor(PhysicalConstants.CLIMBER_ENCODER_TO_METERS)

        climberMotorOne.inverted = true

        motorOneConfig.idleMode(SparkBaseConfig.IdleMode.kBrake)
        motorTwoConfig.idleMode(SparkBaseConfig.IdleMode.kBrake)

        climberMotorOne.encoder.position = 0.0
        climberMotorTwo.encoder.position = 0.0

        motorOneConfig.smartCurrentLimit(26)
        motorTwoConfig.smartCurrentLimit(26)
    }

    override fun periodic() {
        //println(Pair(climberMotorOne.encoder.position, climberMotorTwo.encoder.position))

        stallingOne = if (currentSpeedOne >= TuningConstants.CLIMBER_STALL_TRY_POWER) {
            filterOne.calculate(-climberMotorOne.encoder.velocity <= TuningConstants.CLIMBER_STALL_SPEED)
        } else if (-currentSpeedOne <= -TuningConstants.CLIMBER_STALL_SPEED) {
            filterOne.calculate(false)
        } else {
            filterOne.calculate(stallingOne)
        }
        stallingTwo = if (currentSpeedTwo >= TuningConstants.CLIMBER_STALL_TRY_POWER) {
            filterTwo.calculate(-climberMotorTwo.encoder.velocity <= TuningConstants.CLIMBER_STALL_SPEED)
        } else if (-currentSpeedTwo <= -TuningConstants.CLIMBER_STALL_SPEED) {
            filterTwo.calculate(false)
        } else {
            filterTwo.calculate(stallingTwo)
        }


    }
    fun setSpeed(speedOne:Double, speedTwo:Double){
        climberMotorOne.set(speedOne)
        climberMotorTwo.set(speedTwo)
        currentSpeedOne = speedOne
        currentSpeedTwo = speedTwo
    }
    fun setVoltage(voltageOne:Double, voltageTwo:Double){
        climberMotorOne.setVoltage(voltageOne)
        climberMotorTwo.setVoltage(voltageTwo)
    }

    fun getEncoder(encoderNumber:Int):Double{
        return if (encoderNumber == 1){
            climberMotorOne.encoder.position
        } else {
            climberMotorTwo.encoder.position
        }
    }

    fun getStall(motorNumber:Int):Boolean{
        return if (motorNumber == 1){
            stallingOne
        } else {
            stallingTwo
        }
    }

    fun reset(){
        climberMotorOne.encoder.position = 0.0
        climberMotorTwo.encoder.position = 0.0
    }

    fun stop(){
        climberMotorOne.stopMotor()
        climberMotorTwo.stopMotor()
        currentSpeedOne = 0.0
        currentSpeedTwo = 0.0
    }

    fun setCurrentLimit(current:Int){
        motorOneConfig.smartCurrentLimit(current)
        motorTwoConfig.smartCurrentLimit(current)
    }

    fun getCurrents():Pair<Double, Double>{
        return Pair(climberMotorOne.outputCurrent, climberMotorTwo.outputCurrent)
    }
}
