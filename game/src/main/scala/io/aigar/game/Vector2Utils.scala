package io.aigar.game

import scala.util.Random
import scala.math.{Pi, cos, sin}
import com.github.jpbetz.subspace._
import io.aigar.game.serializable.Position

object Vector2Utils {
  def randomUnitVector: Vector2 = {
    val angle = Random.nextFloat * 2f * Pi.toFloat
    Vector2(cos(angle).toFloat, sin(angle).toFloat)
  }

  implicit class Vector2Addons(val vector: Vector2) {
    def state = {
      serializable.Position(vector.x, vector.y)
    }
    
    def truncate(length: Float): Vector2 = {
      if (vector.magnitude > length) vector.normalize * length else vector
    }

    def safeNormalize: Vector2 = {
      if (vector.magnitude > 0f) vector.normalize else vector
    }

    /**
     * Perpendicular (clockwise) version of the vector.
     */
    def perpendicular: Vector2 = {
      return Vector2(-vector.y, vector.x)
    }
  }
}

object Position2Utils {
  implicit class PositionAddon(val position: Position) {
    def toVector = {
      Vector2(position.x, position.y)
    }
  }
}
