package org.sert2521.crescendo2024.subsystems

import com.revrobotics.spark.SparkBase
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import edu.wpi.first.wpilibj.DutyCycleEncoder
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.crescendo2024.ElectronicIDs
import org.sert2521.crescendo2024.PhysicalConstants
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.commands.SetWrist
import kotlin.math.PI

object Wrist : SubsystemBase() {
    val motorOne = SparkMax(ElectronicIDs.WRIST_ONE_ID, SparkLowLevel.MotorType.kBrushless)
    val motorTwo = SparkMax(ElectronicIDs.WRIST_TWO_ID, SparkLowLevel.MotorType.kBrushless)

    private val motorOneConfig = SparkMaxConfig()
    private val motorTwoConfig = SparkMaxConfig()

    val encoder = motorOne.encoder
    val absEncoder = DutyCycleEncoder(ElectronicIDs.ABSOLUTE_ENCODER_ID)
    val motorSpeed = 0.0
    var prevRot = 0.0
    var deltaTime = Timer.getFPGATimestamp()
    var vel = 0.0

    init{
        motorOneConfig.smartCurrentLimit(30)
        motorTwoConfig.smartCurrentLimit(30)
        //defaultCommand= RunWrist()
        //motor.inverted = true

        prevRot = getRadians()
        motorOneConfig.idleMode(SparkBaseConfig.IdleMode.kBrake)
        motorTwoConfig.idleMode(SparkBaseConfig.IdleMode.kBrake)

        motorOneConfig.inverted(false)
        motorTwoConfig.inverted(true)

        motorOne.configure(motorOneConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters)
        motorTwo.configure(motorTwoConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters)


        val holdCommand = Commands.runOnce({ SetWrist(RuntimeConstants.wristSetPoint, false).schedule() }, this)
        defaultCommand = holdCommand
    }

    override fun periodic(){
        //val time = Timer.getFPGATimestamp()
        //val = time
        //println(getRadians())
    }

    fun rezeroEncoder(){
        //absEncoder.reset() --Don't know what to replace this with yet
    }
    fun setSpeed(speed:Double){
        motorOne.set(speed)
        motorTwo.set(speed)
    }

    fun setVoltage(voltage:Double){
        println(voltage)
        //motorOne.setVoltage(voltage)
        //motorTwo.setVoltage(voltage)
    }

    fun getEncoder():Double{
        //println(absEncoder.get())
        return absEncoder.get() * PhysicalConstants.WRIST_ENCODER_MULTIPLY
    }

    fun getRadians():Double{
        val wristAngle = getEncoder() + PhysicalConstants.WRIST_ENCODER_OFFSET
        //println(wristAngle)


        return wristAngle
    }

    fun getVelocity():Double{
        return getRadians()- prevRot
    }

    fun getAmps():Pair<Double, Double>{
        return Pair(motorOne.outputCurrent, motorTwo.outputCurrent)
    }

    fun stop(){
        motorOne.stopMotor()
        motorTwo.stopMotor()
    }

    fun setCurrentLimit(first:Int){
        motorOneConfig.smartCurrentLimit(first)
        motorTwoConfig.smartCurrentLimit(first)
    }
}
