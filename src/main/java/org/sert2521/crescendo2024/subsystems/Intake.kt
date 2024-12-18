package org.sert2521.crescendo2024.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.crescendo2024.ElectronicIDs

object Intake : SubsystemBase() {
    private val intakeMotor = CANSparkMax(ElectronicIDs.INTAKE_MOTOR_ID, CANSparkLowLevel.MotorType.kBrushless)
    private val alignmentMotor = CANSparkMax(ElectronicIDs.INTAKE_ALIGNMENT_MOTOR_ID, CANSparkLowLevel.MotorType.kBrushless)
    init {
        intakeMotor.idleMode = CANSparkBase.IdleMode.kBrake
        intakeMotor.setSmartCurrentLimit(40)
        intakeMotor.inverted = false
        alignmentMotor.idleMode = CANSparkBase.IdleMode.kBrake
        alignmentMotor.setSmartCurrentLimit(30)
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