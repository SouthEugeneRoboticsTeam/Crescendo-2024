package org.sert2521.crescendo2024.subsystems

import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.sert2521.crescendo2024.ElectronicIDs

object Climber : SubsystemBase() {
    private val climberMotorOne = CANSparkMax(ElectronicIDs.CLIMBER_MOTOR_ONE, CANSparkLowLevel.MotorType.kBrushless)
    private val climberMotorTwo = CANSparkMax(ElectronicIDs.CLIMBER_MOTOR_TWO, CANSparkLowLevel.MotorType.kBrushless)
    init{

    }
    fun setSpeed(speedOne:Double, speedTwo:Double){
        climberMotorOne.set(speedOne)
        climberMotorTwo.set(speedTwo)
    }
    fun setVoltage(voltageOne:Double, voltageTwo:Double){
        climberMotorOne.setVoltage(voltageOne)
        climberMotorTwo.setVoltage(voltageTwo)
    }
    fun stop(){
        climberMotorOne.stopMotor()
        climberMotorTwo.stopMotor()
    }
}