package org.sert2521.crescendo2024

import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController


object Input {
    private val driverController = XboxController(0)
    private val gunnerController = Joystick(1)


    var secondarySpeedMode = false
    fun getSecondarySpeed(): Double {
        return if (!secondarySpeedMode) {
            driverController.leftTriggerAxis
        } else {
            1.0
        }
    }

    fun getX(): Double {
        return -driverController.leftY
    }

    fun getY(): Double {
        return -driverController.leftX
    }

    fun getRot(): Double {
        return -driverController.rightX
    }
}