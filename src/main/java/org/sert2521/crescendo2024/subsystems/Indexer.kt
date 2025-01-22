package org.sert2521.crescendo2024.subsystems

import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.sert2521.crescendo2024.ElectronicIDs

object Indexer : Subsystem{
    private val indexerMotor = SparkMax(ElectronicIDs.INDEXER_MOTOR_ID, SparkLowLevel.MotorType.kBrushless)
    private val beamBreakSensor = DigitalInput(ElectronicIDs.BEAMBREAK_ID)
    private val config = SparkMaxConfig()
    init{
        config.idleMode(SparkBaseConfig.IdleMode.kBrake)
        config.smartCurrentLimit(30)
        config.inverted(true)
    }

    fun getBeamBreak():Boolean{
        return !beamBreakSensor.get()
    }

    fun setMotor(speed:Double){
        indexerMotor.set(speed)
    }

    fun getAmps():Double{
        return indexerMotor.outputCurrent
    }

    fun stop(){
        indexerMotor.stopMotor()
    }
}