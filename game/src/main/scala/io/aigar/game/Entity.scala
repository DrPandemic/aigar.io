package io.aigar.game

import com.github.jpbetz.subspace.Vector2

trait Entity {
  var position = new Vector2(0, 0)
  var mass = 0
}
