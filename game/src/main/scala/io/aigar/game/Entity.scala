package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.game.Vector2Utils.Vector2Addons

object Entity {
  /**
   * How much (ratio) of an entity's diameter must be covered by another entity
   * to consider that it is being overlapped.
   */
  val OverlappingRatio = 0.85f

  /**
   * Ratio of the diameter to reach the middle of an entity. Use this value to
   * require overlapping only the center of an entity.
   */
  val CenterRatio = 0.5f
}

trait Entity {
  var position: Vector2
  protected var _mass = 0f

  // Score to be added to the player when it collides with the entity
  val _scoreModification: Float = 0f
  def scoreModification(): Float = {
    _scoreModification
  }

  def mass: Float = _mass
  def mass_=(m: Float): Unit = {
    _mass = m
  }

  def radius: Float

  def overlaps(other: Entity): Boolean = {
    val dir = (other.position - position).safeNormalize
    val ratioPastCenter = Entity.OverlappingRatio - Entity.CenterRatio
    val target = other.position + dir * ratioPastCenter
    contains(target)
  }

  def contains(pos: Vector2): Boolean = {
    position.distanceTo(pos) <= radius
  }
}
