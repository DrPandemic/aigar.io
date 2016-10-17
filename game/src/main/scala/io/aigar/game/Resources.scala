package io.aigar.game

import io.aigar.game.serializable.Position

class Resources {
  var regular = List(new Position(12, 12))
  var silver = List(new Position(112, 112))
  var gold = List(new Position(212, 212))

  def state = {
    serializable.Resources(
      regular,
      silver,
      gold
    )
  }
}
