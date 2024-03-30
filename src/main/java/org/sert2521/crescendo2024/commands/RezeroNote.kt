package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import org.sert2521.crescendo2024.subsystems.Indexer

class RezeroNote : Command() {
    private var beamBreakStatus = false


    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(Indexer)
    }

    override fun initialize() {
        if (!Indexer.getBeamBreak()){
            this.cancel()
        }
        beamBreakStatus = false
    }

    override fun execute() {
        if (!beamBreakStatus){
            Indexer.setMotor(-0.3)
            beamBreakStatus=!Indexer.getBeamBreak()
        } else {
            Indexer.setMotor(0.3)
        }

    }

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return beamBreakStatus&&Indexer.getBeamBreak()
    }

    override fun end(interrupted: Boolean) {
        Indexer.stop()
        beamBreakStatus = false
    }
}
