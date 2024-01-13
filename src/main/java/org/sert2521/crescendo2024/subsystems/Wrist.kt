package org.sert2521.bunnybots2023.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkLowLevel
import edu.wpi.first.wpilibj.DutyCycleEncoder
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.commands.*
import java.beans.Encoder
import kotlin.math.PI
import kotlin.math.abs

object Wrist : SubsystemBase() {
    val motor = CANSparkMax(-1, CANSparkLowLevel.MotorType.kBrushless)

    val encoder = motor.encoder
    val absEncoder = DutyCycleEncoder(-1)
    val motorSpeed = 0.0

    init{
        motor.setSmartCurrentLimit(40)
        //defaultCommand= RunWrist()
        //motor.inverted = true

        absEncoder.distancePerRotation = PhysicalConstants.WRIST_ENCODER_MULTIPLY
    }
    fun setSpeed(speed:Double){
        motor.set(speed)
    }

    fun setVoltage(voltage:Double){
        motor.setVoltage(voltage)
    }

    fun getEncoder():Double{
        return absEncoder.get()
    }

    fun getRadians():Double{
        var wristAngle = (absEncoder.distance+PI).mod(2*PI) - PI + PhysicalConstants.WRIST_ENCODER_OFFSET



        return wristAngle
    }

    fun resetEncoder(){
        absEncoder.reset()
    }

    fun stop(){
        motor.stopMotor()
    }
}
