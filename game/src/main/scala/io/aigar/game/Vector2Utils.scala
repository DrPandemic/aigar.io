package io.aigar.game

import com.github.jpbetz.subspace._
import io.aigar.game.serializable.Position

object Vector2Utils {
  implicit class StateAddon(val vector: Vector2) {
    def state = {
      serializable.Position(vector.x, vector.y)
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
