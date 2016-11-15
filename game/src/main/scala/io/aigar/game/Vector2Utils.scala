package io.aigar.game

import com.github.jpbetz.subspace._
import io.aigar.game.serializable.Position

object Vector2Utils {
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
  }
}

object Position2Utils {
  implicit class PositionAddon(val position: Position) {
    def toVector = {
      Vector2(position.x, position.y)
    }
  }
}
