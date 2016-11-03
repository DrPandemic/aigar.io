package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.game.serializable.Position

object Virus {
  final val Quantity = 15
  final val Mass = 100
}

class Virus(position: Vector2) {

  def state: Position = {
    Vector2Utils.StateAddon(position).state
  }
}
