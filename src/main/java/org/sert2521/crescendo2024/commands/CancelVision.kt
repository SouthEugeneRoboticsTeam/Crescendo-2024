package org.sert2521.crescendo2024.commands

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import org.sert2521.crescendo2024.RuntimeConstants
import org.sert2521.crescendo2024.subsystems.Vision

class CancelVision : Command() {


    init {
        // each subsystem used by the command must be passed into the addRequirements() method
    }

    override fun initialize() {
        RuntimeConstants.isVisionAuto = false
    }

    override fun execute() {}

    override fun isFinished(): Boolean {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return true
    }

    override fun end(interrupted: Boolean) {}
}
