package org.sert2521.crescendo2024.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkLowLevel
import edu.wpi.first.wpilibj.DutyCycleEncoder
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.crescendo2024.ElectronicIDs
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.commands.SetWrist
import kotlin.math.PI

object Wrist : SubsystemBase() {
    val motorOne = CANSparkMax(ElectronicIDs.WRIST_ONE_ID, CANSparkLowLevel.MotorType.kBrushless)
    val motorTwo = CANSparkMax(ElectronicIDs.WRIST_TWO_ID, CANSparkLowLevel.MotorType.kBrushless)

    val encoder = motorOne.encoder
    val absEncoder = DutyCycleEncoder(-1)
    val motorSpeed = 0.0
    var prevRot = 0.0
    var deltaTime = Timer.getFPGATimestamp()
    var vel = 0.0

    init{
        //motorOne.setSmartCurrentLimit(40)
        //defaultCommand= RunWrist()
        //motor.inverted = true

        absEncoder.distancePerRotation = PhysicalConstants.WRIST_ENCODER_MULTIPLY
        prevRot = getRadians()
        defaultCommand = SetWrist(getRadians(), false)
    }

    override fun periodic(){
        //val time = Timer.getFPGATimestamp()
        //vel = time
    }
    fun setSpeed(speed:Double){
        motorOne.set(speed)
        motorTwo.set(speed)
    }

    fun setVoltage(voltage:Double){
        motorOne.setVoltage(voltage)
        motorTwo.setVoltage(voltage)
    }

    fun getEncoder():Double{
        return absEncoder.get()
    }

    fun getRadians():Double{
        var wristAngle = (absEncoder.distance+PI).mod(2*PI) - PI + PhysicalConstants.WRIST_ENCODER_OFFSET



        return wristAngle
    }

    fun getVelocity():Double{
        return getRadians()- prevRot
    }

    fun resetEncoder(){
        absEncoder.reset()
    }

    fun stop(){
        motorOne.stopMotor()
        motorTwo.stopMotor()
    }
}
