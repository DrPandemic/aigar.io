package io.aigar.game

import com.github.jpbetz.subspace._

object Vector2Utils {
  implicit class StateAddon(val vector: Vector2) {
    def state = {
      serializable.Position(vector.x, vector.y)
    }
  }
}
