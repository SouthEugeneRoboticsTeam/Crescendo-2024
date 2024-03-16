package org.sert2521.crescendo2024

import edu.wpi.first.wpilibj.DataLogManager
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.crescendo2024.subsystems.*
import java.io.File

object Output : SubsystemBase() {
    private val values = mutableListOf<Pair<String, () -> Double>>()
    private val bools = mutableListOf<Pair<String, () -> Boolean>>()
    private val field = Field2d()
    private var drivetrainAmps: Array<Pair<Double, Double>> = arrayOf()
    private var flywheelAmps = Flywheel.getAmps()
    private var wristAmps = Wrist.getAmps()
    private var climberAmps = Climber.getCurrents()
    private var indexerAmps = Indexer.getAmps()
    private var intakeAmps = Intake.getAmps()
    private var totalAmps = 0.0
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

        values.add(Pair("Drive 1 Reference Drive", { Drivetrain.getReferences()[0] }))
        values.add(Pair("Drive 2 Reference Drive", { Drivetrain.getReferences()[1] }))
        values.add(Pair("Drive 3 Reference Drive", { Drivetrain.getReferences()[2] }))
        values.add(Pair("Drive 4 Reference Drive", { Drivetrain.getReferences()[3] }))

        values.add(Pair("Drive 1 Amps Drive", { drivetrainAmps[0].first }))
        values.add(Pair("Drive 2 Amps Drive", { drivetrainAmps[1].first }))
        values.add(Pair("Drive 3 Amps Drive", { drivetrainAmps[2].first }))
        values.add(Pair("Drive 4 Amps Drive", { drivetrainAmps[3].first }))

        values.add(Pair("Drive 1 Amps Angle", { drivetrainAmps[0].second }))
        values.add(Pair("Drive 2 Amps Angle", { drivetrainAmps[1].second }))
        values.add(Pair("Drive 3 Amps Angle", { drivetrainAmps[2].second }))
        values.add(Pair("Drive 4 Amps Angle", { drivetrainAmps[3].second }))

        values.add(Pair("Wrist Angle", { Wrist.getRadians() }))

        values.add(Pair("Wrist 1 Amps", { wristAmps.first }))
        values.add(Pair("Wrist 2 Amps", { wristAmps.second }))

        values.add(Pair("Flywheel Speed", { Flywheel.getSpeeds().first }))
        values.add(Pair("Flywheel Goal", { RuntimeConstants.flywheelGoal }))

        values.add(Pair("Flywheel 1 Amps", { flywheelAmps.first }))
        values.add(Pair("Flywheel 2 Amps", { flywheelAmps.second }))

        values.add(Pair("Climber 1 Amps", { climberAmps.first }))
        values.add(Pair("Climber 2 Amps", { climberAmps.second }))

        values.add(Pair("Indexer Amps", { indexerAmps }))

        values.add(Pair("Intake intake Amps", { intakeAmps.first }))
        values.add(Pair("Intake alignment", { intakeAmps.second }))

        values.add(Pair("Total Amps", { totalAmps }))

        bools.add(Pair("Beambreak", { Indexer.getBeamBreak() }))

        SmartDashboard.putData("Field", field)
    }
    fun update(){
        drivetrainAmps = Drivetrain.getAmps()
        flywheelAmps = Flywheel.getAmps()
        wristAmps = Wrist.getAmps()
        climberAmps = Climber.getCurrents()
        indexerAmps = Indexer.getAmps()
        intakeAmps = Intake.getAmps()
        totalAmps = drivetrainAmps[0].first+drivetrainAmps[1].first+drivetrainAmps[2].first+drivetrainAmps[3].first+drivetrainAmps[0].second+drivetrainAmps[1].second+drivetrainAmps[2].second+drivetrainAmps[3].second+flywheelAmps.first+flywheelAmps.second+wristAmps.first+wristAmps.second+climberAmps.first+climberAmps.second+indexerAmps+intakeAmps.first+intakeAmps.second


        for (value in values) {
            SmartDashboard.putNumber("Output/${value.first}", value.second())
        }

        for (bool in bools) {
            SmartDashboard.putBoolean("Output/${bool.first}", bool.second())
        }

        field.robotPose = Drivetrain.getPose()
    }
}