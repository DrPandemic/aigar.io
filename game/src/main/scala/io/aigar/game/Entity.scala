package io.aigar.game

import com.github.jpbetz.subspace.Vector2

trait Entity {
  var position: Vector2
  protected var _mass = 0f

  def mass: Float = _mass
  def mass_=(m: Float): Unit = {
    _mass
  }
}
