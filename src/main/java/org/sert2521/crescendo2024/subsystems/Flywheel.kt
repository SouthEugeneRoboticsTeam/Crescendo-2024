package org.sert2521.crescendo2024.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.filter.Debouncer
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.sert2521.crescendo2024.*
import org.sert2521.crescendo2024.commands.SetFlywheel
import org.sert2521.crescendo2024.commands.SetWrist
import java.util.logging.Filter

object Flywheel : SubsystemBase(){
    private val flywheelMotorOne = CANSparkMax(ElectronicIDs.FLYWHEEL_MOTOR_ONE_ID, CANSparkLowLevel.MotorType.kBrushless) //Top
    private val flywheelMotorTwo = CANSparkMax(ElectronicIDs.FLYWHEEL_MOTOR_TWO_ID, CANSparkLowLevel.MotorType.kBrushless) //Bottom
    private val currentFilter = Debouncer(0.2)
    private var currentCurrentLimit = 40
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

    override fun periodic(){
        val shouldLimit = currentFilter.calculate(Drivetrain.getDraw()>150)

        if (Robot.isAutonomous && currentCurrentLimit!=50){
            flywheelMotorOne.setSmartCurrentLimit(50)
            flywheelMotorTwo.setSmartCurrentLimit(50)
            currentCurrentLimit=50
        } else if (currentCurrentLimit!=20){
            flywheelMotorOne.setSmartCurrentLimit(20)
            flywheelMotorTwo.setSmartCurrentLimit(20)
            currentCurrentLimit=20
        }
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