package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.subsystems.Indexer
import org.sert2521.crescendo2024.subsystems.Intake

class IntakeCommand : Command() {
    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Indexer, Intake)
    }

    override fun initialize() {
        /*
        if (Wrist.getRadians()>-0.1){
            this.end(true)
        } else {
            Intake.setMotor(0.9)
            Indexer.setMotor(0.5)
        }
         */
        Intake.setMotor(0.9)
        Indexer.setMotor(0.5)
    }

    override fun execute() {
        /*
        if (Wrist.getRadians()>-0.1){
            this.end(true)
        } else {
            Intake.setMotor(0.9)
            Indexer.setMotor(0.5)
        }

         */
        Intake.setMotor(0.9)
        Indexer.setMotor(0.5)
    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute(
        return Indexer.getBeamBreak()
        //return false
    }

    override fun end(interrupted: Boolean) {
        Intake.stop()
        Indexer.stop()
    }
}
