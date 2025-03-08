package org.sert2521.reefscape2025.commands.drivetrain

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.sert2521.crescendo2024.ConfigConstants
import org.sert2521.crescendo2024.commands.ReadJoysticks
import org.sert2521.crescendo2024.subsystems.Drivetrain
import org.sert2521.reefscape2025.subsystems.elevator.Elevator
import kotlin.math.*

class JoystickDrive(private val fieldOriented:Boolean = true) : ReadJoysticks() {

    private var joystickAccelLimited = ChassisSpeeds()

    // Requirements are already passed through the ReadJoysticks class

    override fun execute() {
        if (fieldOriented) {
            joystickAccelLimited = readJoysticks(
                ConfigConstants.DRIVE_ACCEL, Rotation2d(super.inputRotOffset),
                ConfigConstants.DRIVE_DECCEL, ConfigConstants.DRIVE_SPEED)

            if (joystickAccelLimited.vxMetersPerSecond == 0.0 && joystickAccelLimited.vyMetersPerSecond == 0.0 && joystickAccelLimited.omegaRadiansPerSecond == 0.0){
                Drivetrain.stop()
            }
            Drivetrain.drive(ChassisSpeeds())
        }
        else {
            joystickAccelLimited = readJoysticks(ConfigConstants.DRIVE_ACCEL, Rotation2d(),
                ConfigConstants.DRIVE_DECCEL, ConfigConstants.DRIVE_SPEED, false)

            if (joystickAccelLimited.vxMetersPerSecond == 0.0 && joystickAccelLimited.vyMetersPerSecond == 0.0 && joystickAccelLimited.omegaRadiansPerSecond == 0.0){
                Drivetrain.stop()
            }

            Drivetrain.drive(ReadJoysticks.)
        }
    }
}
