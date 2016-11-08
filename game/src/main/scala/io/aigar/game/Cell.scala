package io.aigar.game

import io.aigar.controller.response.Action
import scala.math.{max, round, pow}
import io.aigar.game.Vector2Utils.StateAddon
import io.aigar.game.Position2Utils.PositionAddon
import com.github.jpbetz.subspace.Vector2
import scala.math._

object Cell {
  /**
   * Force (in Newtons) applied when moving.
   */
  final val MovementForce = 350f

  /**
   * Default mass of a cell (at spawn).
   */
  final val MinMass = 20f

  /**
   * Ratio of mass lost per second.
   */
  final val MassDecayPerSecond = 0.005f

  /**
    * Exponent used on the mass when calculating the acceleration of a cell.
    */
  final val MassImpactOnAcceleration = 0.75f
}

class Cell(val id: Int, player: Player, startPosition: Vector2 = new Vector2(0f, 0f)) {
  var position = startPosition
  var target = startPosition
  var _mass = Cell.MinMass
  private var _velocity = new Vector2(0f, 0f)

  /**
   * The maximum speed (length of the velocity) for the cell, in units per
   * second.
   */
  def maxSpeed: Float = {
    50f //TODO depend on mass?
  }

  def velocity: Vector2 = _velocity
  def velocity_=(vel:Vector2): Unit = {
    _velocity = if (vel.magnitude < maxSpeed) vel else vel.normalize * maxSpeed
  }
  def mass: Float = _mass
  def mass_=(m: Float): Unit = {
    _mass = max(m, Cell.MinMass)
  }

  def radius: Double = {
    4 + sqrt(mass) * 3
  }

  def update(deltaSeconds: Float, grid: Grid): Unit = {
    mass = decayedMass(deltaSeconds)

    target = player.aiState.update(deltaSeconds, grid, this)

    velocity += acceleration * deltaSeconds
    position += velocity * deltaSeconds

    keepInGrid(grid)
  }

  def keepInGrid(grid: Grid): Unit = {
    if (position.x <= 0 || position.x >= grid.width){
      velocity = new Vector2(0f, velocity.y)
    }
    if (position.y <= 0 || position.y >= grid.height){
      velocity = new Vector2(velocity.x, 0f)
    }

    position = position.clamp(new Vector2(0f, 0f), new Vector2(grid.width, grid.height))
  }

  def decayedMass(deltaSeconds: Float): Float = {
    mass * pow(1f - Cell.MassDecayPerSecond, deltaSeconds).toFloat
  }

  def acceleration: Vector2 = {
    val dir = target - position
    val value = Cell.MovementForce / pow(mass, Cell.MassImpactOnAcceleration).toFloat
    if (dir.magnitude > 0) dir.normalize * value else new Vector2(0f,0f)
  }

  def contains(pos: Vector2): Boolean = {
    position.distanceTo(pos) <= radius
  }

  def eats(opponents: List[Player]): Unit ={
    for(opponent <- opponents) {
      for(cell <- opponent.cells) {
        if (contains(cell.position) && mass >= 1.1 * cell.mass) { //Cell must be 10% larger to eat it
          mass = mass + cell.mass
          opponent.removeCell(cell)
        }
      }
    }
  }

  def performAction(action: Action): Unit = {
    target = action.target.toVector
  }

  def state: serializable.Cell = {
    serializable.Cell(id,
                      round(mass),
                      round(radius).toInt,
                      position.state,
                      target.state)
  }
}
