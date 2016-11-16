package io.aigar.game

import com.github.jpbetz.subspace.Vector2

trait Entity {
  var position: Vector2
  protected var _mass = 0f
  
  // Score to be added to the player when it collides with the entity
  val scoreModification: Int

  def mass: Float = _mass
  def mass_=(m: Float): Unit = {
    _mass = m
  }
}
