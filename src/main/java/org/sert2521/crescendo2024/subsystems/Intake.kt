package org.sert2521.crescendo2024.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkLowLevel
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.crescendo2024.ElectronicIDs

object Intake : SubsystemBase() {
    private val intakeMotor = CANSparkMax(ElectronicIDs.INTAKE_MOTOR_ID, CANSparkLowLevel.MotorType.kBrushless)

    init {
        intakeMotor.idleMode = CANSparkBase.IdleMode.kBrake
        intakeMotor.setSmartCurrentLimit(10, 45)
    }
    fun setMotor(speed:Double) {
        intakeMotor.set(speed)
    }
    fun stop(){
        intakeMotor.stopMotor()
    }
}