package org.sert2521.crescendo2024.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.sert2521.crescendo2024.ElectronicIDs
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.commands.SetFlywheel
import org.sert2521.crescendo2024.commands.SetWrist

object Flywheel : SubsystemBase(){
    private val flywheelMotorOne = CANSparkMax(ElectronicIDs.FLYWHEEL_MOTOR_ONE_ID, CANSparkLowLevel.MotorType.kBrushless) //Top
    private val flywheelMotorTwo = CANSparkMax(ElectronicIDs.FLYWHEEL_MOTOR_TWO_ID, CANSparkLowLevel.MotorType.kBrushless) //Bottom

    init{
        flywheelMotorOne.encoder.positionConversionFactor = PhysicalConstants.FLYWHEEL_GEAR_RATIO
        flywheelMotorOne.encoder.velocityConversionFactor = PhysicalConstants.FLYWHEEL_GEAR_RATIO
        flywheelMotorOne.idleMode = CANSparkBase.IdleMode.kCoast
        flywheelMotorOne.inverted = false
        flywheelMotorTwo.encoder.positionConversionFactor = PhysicalConstants.FLYWHEEL_GEAR_RATIO
        flywheelMotorTwo.encoder.velocityConversionFactor = PhysicalConstants.FLYWHEEL_GEAR_RATIO
        flywheelMotorTwo.idleMode = CANSparkBase.IdleMode.kCoast
        flywheelMotorTwo.inverted = false
        flywheelMotorOne.setSmartCurrentLimit(40)
        flywheelMotorTwo.setSmartCurrentLimit(40)
        //defaultCommand = SetFlywheel(TuningConstants.FLYWHEEL_IDLE_SPEED)
        val holdCommand = InstantCommand({ if (RuntimeConstants.flywheelGoal != 0.0){
            SetFlywheel(RuntimeConstants.flywheelGoal, false).schedule() }})
        holdCommand.addRequirements(this)
        defaultCommand = holdCommand
    }
    fun getSpeeds():Pair<Double, Double>{
        return Pair(flywheelMotorOne.encoder.velocity,flywheelMotorTwo.encoder.velocity)
    }
    fun setVoltages(voltages:Pair<Double, Double>){
        flywheelMotorOne.setVoltage(voltages.first)
        flywheelMotorTwo.setVoltage(voltages.second)
    }
    fun setSpeed(speed:Double){
        flywheelMotorOne.set(speed)
        flywheelMotorTwo.set(speed)
    }

    fun getAmps():Pair<Double, Double>{
        return Pair(flywheelMotorOne.outputCurrent, flywheelMotorTwo.outputCurrent)
    }
    fun stop(){
        flywheelMotorOne.stopMotor()
        flywheelMotorTwo.stopMotor()
    }
}