package org.sert2521.crescendo2024

import edu.wpi.first.apriltag.AprilTagFieldLayout
import edu.wpi.first.apriltag.AprilTagFields
import edu.wpi.first.math.MatBuilder.fill
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.Nat
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Transform3d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.util.Units
import kotlin.math.PI


//gear ratio means (1/gear ratio) equals positionConversionFactor
/*
 * The Constants file provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This file should not be used for any other purpose.
 * All String, Boolean, and numeric (Int, Long, Float, Double) constants should use
 * `const` definitions. Other constant types should use `val` definitions.
 */
