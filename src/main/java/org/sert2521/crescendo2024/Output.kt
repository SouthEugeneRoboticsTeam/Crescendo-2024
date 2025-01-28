package org.sert2521.crescendo2024

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DataLogManager
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.crescendo2024.subsystems.*
import org.sert2521.crescendo2024.subsystems.Drivetrain
import java.io.File
import kotlin.jvm.optionals.getOrNull

object Output : SubsystemBase() {
    private val values = mutableListOf<Pair<String, () -> Double>>()
    private val bools = mutableListOf<Pair<String, () -> Boolean>>()
    private var wristAmps = Wrist.getAmps()
    init {

        val storageDevices = File("/media").listFiles()
        if (storageDevices != null) {
            if (storageDevices.isNotEmpty()) {
                DataLogManager.start(storageDevices[0].absolutePath)
                DriverStation.startDataLog(DataLogManager.getLog())
            }
        }

        values.add(Pair("Drive 1 Speed Drive") { Drivetrain.getStates()[0].speedMetersPerSecond })
        values.add(Pair("Drive 2 Speed Drive") { Drivetrain.getStates()[1].speedMetersPerSecond })
        values.add(Pair("Drive 3 Speed Drive") { Drivetrain.getStates()[2].speedMetersPerSecond })
        values.add(Pair("Drive 4 Speed Drive") { Drivetrain.getStates()[3].speedMetersPerSecond })

        values.add(Pair("Wrist 1 Amps") { wristAmps.first })
        values.add(Pair("Wrist 2 Amps") { wristAmps.second })

        values.add(Pair("Flywheel Speed 1") { Flywheel.getSpeeds().first })
        values.add(Pair("Flywheel Speed 2") { Flywheel.getSpeeds().second })

        bools.add(Pair("Beambreak") { Indexer.getBeamBreak() })

        update()
    }
    fun update(){

        for (value in values) {
            SmartDashboard.putNumber("Output/${value.first}", value.second())
        }

        for (bool in bools) {
            SmartDashboard.putBoolean("Output/${bool.first}", bool.second())
        }

        SmartDashboard.updateValues()
    }
}