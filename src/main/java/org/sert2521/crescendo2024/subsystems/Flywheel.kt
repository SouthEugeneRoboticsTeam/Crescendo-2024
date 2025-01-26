package org.sert2521.crescendo2024.subsystems


import com.revrobotics.spark.SparkBase
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import edu.wpi.first.math.filter.Debouncer
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.sert2521.crescendo2024.*
import org.sert2521.crescendo2024.commands.SetFlywheel
import org.sert2521.crescendo2024.subsystems.Drivetrain

object Flywheel : SubsystemBase(){
    private val flywheelMotorOne = SparkMax(ElectronicIDs.FLYWHEEL_MOTOR_ONE_ID, SparkLowLevel.MotorType.kBrushless) //Top
    private val flywheelMotorTwo = SparkMax(ElectronicIDs.FLYWHEEL_MOTOR_TWO_ID, SparkLowLevel.MotorType.kBrushless) //Bottom
    private val currentFilter = Debouncer(0.2)
    private var currentCurrentLimit = 40
    private val motorOneConfig = SparkMaxConfig()
    private val motorTwoConfig = SparkMaxConfig()
    init{
        motorOneConfig.inverted(false)
        motorOneConfig.smartCurrentLimit(currentCurrentLimit)
        motorOneConfig.idleMode(SparkBaseConfig.IdleMode.kCoast)
        flywheelMotorOne.configure(motorOneConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters)
        motorTwoConfig.smartCurrentLimit(currentCurrentLimit)
        motorTwoConfig.idleMode(SparkBaseConfig.IdleMode.kCoast)
        flywheelMotorTwo.configure(motorTwoConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters)
        motorOneConfig.encoder.positionConversionFactor(PhysicalConstants.FLYWHEEL_GEAR_RATIO)
        motorOneConfig.encoder.velocityConversionFactor(PhysicalConstants.FLYWHEEL_GEAR_RATIO)
        motorTwoConfig.encoder.positionConversionFactor(PhysicalConstants.FLYWHEEL_GEAR_RATIO)
        motorTwoConfig.encoder.velocityConversionFactor(PhysicalConstants.FLYWHEEL_GEAR_RATIO)
        //defaultCommand = SetFlywheel(TuningConstants.FLYWHEEL_IDLE_SPEED)
        val holdCommand = InstantCommand({ if (RuntimeConstants.flywheelGoal != 0.0){
            SetFlywheel(RuntimeConstants.flywheelGoal, false).schedule() }})
        holdCommand.addRequirements(this)
        defaultCommand = holdCommand
    }

    override fun periodic(){
        val shouldLimit = currentFilter.calculate(Drivetrain.getDraw()>150)

        if (Robot.isAutonomous && currentCurrentLimit!=50){
            motorOneConfig.smartCurrentLimit(50)
            motorTwoConfig.smartCurrentLimit(50)
            currentCurrentLimit=50
        } else if (currentCurrentLimit!=20){
            motorOneConfig.smartCurrentLimit(20)
            motorTwoConfig.smartCurrentLimit(20)
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