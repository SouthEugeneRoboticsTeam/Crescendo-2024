package org.sert2521.crescendo2024.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.sert2521.crescendo2024.ElectronicIDs
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.commands.SetFlywheel

object Flywheel : SubsystemBase(){
    private val flywheelMotorOne = CANSparkMax(ElectronicIDs.FLYWHEEL_MOTOR_ONE_ID, CANSparkLowLevel.MotorType.kBrushless) //Top
    private val flywheelMotorTwo = CANSparkMax(ElectronicIDs.FLYWHEEL_MOTOR_TWO_ID, CANSparkLowLevel.MotorType.kBrushless) //Bottom

    init{
        flywheelMotorOne.encoder.positionConversionFactor = PhysicalConstants.FLYWHEEL_GEAR_RATIO
        flywheelMotorOne.encoder.velocityConversionFactor = PhysicalConstants.FLYWHEEL_GEAR_RATIO / 60
        flywheelMotorOne.idleMode = CANSparkBase.IdleMode.kCoast
        flywheelMotorOne.inverted = true
        flywheelMotorTwo.encoder.positionConversionFactor = PhysicalConstants.FLYWHEEL_GEAR_RATIO
        flywheelMotorTwo.encoder.velocityConversionFactor = PhysicalConstants.FLYWHEEL_GEAR_RATIO / 60
        flywheelMotorTwo.idleMode = CANSparkBase.IdleMode.kCoast
        defaultCommand = SetFlywheel(TuningConstants.FLYWHEEL_IDLE_SPEED)
    }
    fun getSpeed():Double{
        return (flywheelMotorOne.encoder.velocity+flywheelMotorTwo.encoder.velocity)/2.0
    }
    fun setVoltage(voltage:Double){
        flywheelMotorOne.setVoltage(voltage)
        flywheelMotorTwo.setVoltage(voltage)
    }
    fun setSpeed(speed:Double){
        flywheelMotorOne.set(speed)
        flywheelMotorTwo.set(speed)
    }
    fun stop(){
        flywheelMotorOne.stopMotor()
        flywheelMotorTwo.stopMotor()
    }
}