package org.sert2521.crescendo2024.subsystems

import com.revrobotics.spark.*
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import edu.wpi.first.wpilibj.CAN
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.crescendo2024.ElectronicIDs

object Intake : SubsystemBase() {
    private val intakeMotor = SparkMax(ElectronicIDs.INTAKE_MOTOR_ID, SparkLowLevel.MotorType.kBrushless)
    private val alignmentMotor = SparkMax(ElectronicIDs.INTAKE_ALIGNMENT_MOTOR_ID, SparkLowLevel.MotorType.kBrushless)
    private val config = SparkMaxConfig()
    init {
        config.idleMode(SparkBaseConfig.IdleMode.kBrake)
        config.inverted(false)
        config.smartCurrentLimit(30)
    }
    fun setMotor(speed:Double) {
        intakeMotor.set(speed)
        alignmentMotor.set(speed)
    }

    fun getAmps():Pair<Double, Double>{
        return Pair(intakeMotor.outputCurrent, alignmentMotor.outputCurrent)
    }
    fun stop(){
        intakeMotor.stopMotor()
        alignmentMotor.stopMotor()
    }
}