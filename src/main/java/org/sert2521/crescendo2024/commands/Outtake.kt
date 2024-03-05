package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.TuningConstants
import org.sert2521.crescendo2024.subsystems.Flywheel
import org.sert2521.crescendo2024.subsystems.Indexer
import org.sert2521.crescendo2024.subsystems.Intake
import org.sert2521.crescendo2024.subsystems.Wrist
import kotlin.math.PI

class Outtake(val withoutRev:Boolean = false) : Command() {
    private var wasRevved = false
    private val flywheelCommand = SetFlywheel(2000.0)

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Indexer, Intake)
    }

    override fun initialize() {
        Indexer.setMotor(0.0)
    }

    override fun execute() {
        if (Wrist.getRadians() >= 1.3){ //Just to check if its in amp range
            flywheelCommand.schedule()
            Indexer.setMotor(1.0)
        } else {
            if (RuntimeConstants.flywheelRevved || withoutRev){
                Indexer.setMotor(1.0)
            }
        }
    }

    override fun isFinished(): Boolean {
        return false
    }

    override fun end(interrupted: Boolean) {
        Indexer.stop()
        flywheelCommand.cancel()
    }
}
