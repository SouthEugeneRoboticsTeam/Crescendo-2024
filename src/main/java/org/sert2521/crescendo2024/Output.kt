package org.sert2521.crescendo2024

import edu.wpi.first.wpilibj.DataLogManager
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.crescendo2024.subsystems.Drivetrain
import org.sert2521.crescendo2024.subsystems.Indexer
import org.sert2521.crescendo2024.subsystems.Wrist
import java.io.File

object Output : SubsystemBase() {
    private val values = mutableListOf<Pair<String, () -> Double>>()
    private val bools = mutableListOf<Pair<String, () -> Boolean>>()
    private val field = Field2d()

    init {

        val storageDevices = File("/media").listFiles()
        if (storageDevices != null) {
            if (storageDevices.isNotEmpty()) {
                DataLogManager.start(storageDevices[0].absolutePath)
                DriverStation.startDataLog(DataLogManager.getLog())
            }
        }


        update()

        values.add(Pair("Drive 1 Goal Drive", { Drivetrain.getGoals()[0].speedMetersPerSecond }))
        values.add(Pair("Drive 2 Goal Drive", { Drivetrain.getGoals()[1].speedMetersPerSecond }))
        values.add(Pair("Drive 3 Goal Drive", { Drivetrain.getGoals()[2].speedMetersPerSecond }))
        values.add(Pair("Drive 4 Goal Drive", { Drivetrain.getGoals()[3].speedMetersPerSecond }))

        values.add(Pair("Drive 1 State Drive", { Drivetrain.getStates()[0].speedMetersPerSecond }))
        values.add(Pair("Drive 2 State Drive", { Drivetrain.getStates()[1].speedMetersPerSecond }))
        values.add(Pair("Drive 3 State Drive", { Drivetrain.getStates()[2].speedMetersPerSecond }))
        values.add(Pair("Drive 4 State Drive", { Drivetrain.getStates()[3].speedMetersPerSecond }))

        values.add(Pair("Wrist Angle", { Wrist.getRadians() }))

        bools.add(Pair("Beambreak", { Indexer.getBeamBreak() }))
    }
    fun update(){
        for (value in values) {
            SmartDashboard.putNumber("Output/${value.first}", value.second())
        }

        for (bool in bools) {
            SmartDashboard.putBoolean("Output/${bool.first}", bool.second())
        }
    }
}