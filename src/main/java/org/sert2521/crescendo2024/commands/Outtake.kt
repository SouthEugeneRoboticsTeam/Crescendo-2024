package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Flywheel
import org.sert2521.crescendo2024.subsystems.Indexer
import org.sert2521.crescendo2024.subsystems.Intake

class Outtake : Command() {
    private var wasRevved = false

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Indexer, Intake)
    }

    override fun initialize() {}

    override fun execute() {
        if(RuntimeConstants.flywheelRevved != false||wasRevved == true){
            wasRevved = true
            Indexer.setMotor(0.5)
        }
    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun end(interrupted: Boolean) {
        Indexer.stop()
    }
}
