package org.sert2521.crescendo2024.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.sert2521.crescendo2024.ElectronicIDs

object Indexer : Subsystem{
    private val indexerMotor = CANSparkMax(ElectronicIDs.INDEXER_MOTOR_ID, CANSparkLowLevel.MotorType.kBrushless)
    private val beamBreakSensor = DigitalInput(ElectronicIDs.BEAMBREAK_ID)
    init{
        indexerMotor.idleMode = CANSparkBase.IdleMode.kBrake
        indexerMotor.setSmartCurrentLimit(10, 45)
        indexerMotor.inverted = true
    }
    fun getBeamBreak():Boolean{
        return !beamBreakSensor.get()
    }
    fun setMotor(speed:Double){
        indexerMotor.set(speed)
    }
    fun stop(){
        indexerMotor.stopMotor()
    }
}