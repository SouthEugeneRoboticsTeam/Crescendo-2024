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
    val flywheelMotor = CANSparkMax(ElectronicIDs.FLYWHEEL_MOTOR, CANSparkLowLevel.MotorType.kBrushless)
    init{
        flywheelMotor.encoder.positionConversionFactor = PhysicalConstants.FLYWHEEL_GEAR_RATIO
        flywheelMotor.encoder.velocityConversionFactor = PhysicalConstants.FLYWHEEL_GEAR_RATIO
        flywheelMotor.idleMode = CANSparkBase.IdleMode.kCoast
        defaultCommand = SetFlywheel(TuningConstants.FLYWHEEL_IDLE_SPEED)
    }
    fun getSpeed():Double{
        return flywheelMotor.encoder.velocity
    }
    fun setVoltage(voltage:Double){
        flywheelMotor.setVoltage(voltage)
    }
    fun setSpeed(speed:Double){
        flywheelMotor.set(speed)
    }
    fun stop(){
        flywheelMotor.stopMotor()
    }
}