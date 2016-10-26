package io.aigar.game

import scala.math.{max, round, pow}
import io.aigar.game.Vector2Utils._
import com.github.jpbetz.subspace._
import scala.math._

object Cell {
  /**
   * Force (in Newtons) applied when moving.
   */
  final val MovementForce = 350f

  /**
   * Default mass of a cell (at spawn).
   */
  final val MinMass = 10f

  /**
   * Ratio of mass lost per second.
   */
  final val MassDecayPerSecond = 0.005f
}

class Cell(id: Int, startPosition: Vector2 = new Vector2(0f, 0f)) {
  var position = startPosition
  var target = startPosition
  var behavior: SteeringBehavior = new NoBehavior(this)
  var _mass = Cell.MinMass
  private var _velocity = new Vector2(0f, 0f)

  /**
   * The maximum speed (length of the velocity) for the cell, in units per
   * second.
   */
  def maxSpeed = {
    50f //TODO depend on mass?
  }

  def velocity = _velocity
  def velocity_=(vel:Vector2) {
    _velocity = if (vel.magnitude < maxSpeed) vel else vel.normalize * maxSpeed
  }
  def mass = _mass
  def mass_=(m: Float) {
    _mass = max(m, Cell.MinMass)
  }

  def update(deltaSeconds: Float, grid: Grid) {
    mass = decayedMass(deltaSeconds)

    target = behavior.update(deltaSeconds, grid)

    velocity += acceleration * deltaSeconds
    position += velocity * deltaSeconds
    position = position.clamp(new Vector2(0f, 0f), new Vector2(grid.width, grid.height))
  }

  def decayedMass(deltaSeconds: Float) = {
    mass * pow(1f - Cell.MassDecayPerSecond, deltaSeconds).toFloat
  }

  def acceleration: Vector2 = {
    val dir = target - position
    val value = Cell.MovementForce / mass
    return if (dir.magnitude > 0) dir.normalize * value else new Vector2(0f,0f)
  }

  def contains(pos: Vector2): Boolean = {
    return position.distanceTo(pos) <= sqrt(mass / Pi)
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

  def state = {
    serializable.Cell(id,
                      round(mass).toInt,
                      position.state,
                      target.state)
  }
}
