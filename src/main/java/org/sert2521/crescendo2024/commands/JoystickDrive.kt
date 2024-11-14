package org.sert2521.crescendo2024.commands

import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.sert2521.crescendo2024.subsystems.Drivetrain

class JoystickDrive(private val fieldOrientated: Boolean) : JoystickCommand() {
    init {
        addRequirements(Drivetrain)
    }

    override fun execute() {
        val joystickData = readJoystick()

        if (joystickData.x == 0.0 && joystickData.y == 0.0 && joystickData.z == 0.0) {
            Drivetrain.stop()
            /*
            if (Input.getBrakePos()) {
                Drivetrain.enterBrakePos()
            } else {
                Drivetrain.stop()
            }
             */
        } else {
            if (fieldOrientated) {
                Drivetrain.drive(ChassisSpeeds.fromFieldRelativeSpeeds(joystickData.x, joystickData.y, joystickData.z, Drivetrain.getPose().rotation))
            } else {
                Drivetrain.drive(ChassisSpeeds(joystickData.x, joystickData.y, joystickData.z))
            }
        }
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }
}