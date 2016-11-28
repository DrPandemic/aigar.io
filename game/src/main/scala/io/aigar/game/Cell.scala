package io.aigar.game

import io.aigar.controller.response.Action
import io.aigar.score.ScoreModification
import io.aigar.game.Vector2Utils.Vector2Addons
import io.aigar.game.Position2Utils.PositionAddon
import com.github.jpbetz.subspace.Vector2
import scala.math._

object Cell {
  /**
   * Default mass of a cell (at spawn).
   *
   * IMPORTANT must stay in sync with the client's documentation
   */
  final val MinMass = 20f

  /**
   * Ratio of mass lost per second.
   */
  final val MassDecayPerSecond = 0.005f

  /**
   * How much bigger a cell must be to eat an enemy cell (ratio).
   *
   * IMPORTANT keep this value in sync with the client documentation
   */
  final val MassDominanceRatio = 1.1f

  /**
    * How much a unit of mass represent when traded for score
    *
    * IMPORTANT keep this value in sync with the client documentation
    */
  final val MassToScoreRatio = 0.5f

  final val MinMaximumSpeed = 25f
  final val MaxMaximumSpeed = 50f
  final val SpeedLimitReductionPerMassUnit = 0.02f

  final val RespawnRetryAttempts = 15

  /**
   * When the velocity of a cell goes above its "max speed", a "drag" force is
   * simulated to remove a ratio of the velocity excess. This constant controls
   * what ratio is removed from the excess, per second.
   */
  final val ExcessVelocityReductionPerSec = 0.25f

  /**
   * How much mass is required to gain a small burst of movement.
   *
   * IMPORTANT keep this value in sync with the client documentation
   */
  final val BurstMassCost = 1f

  /**
   * Bursting works by adding a certain amount of times the force that is
   * normally applied when moving. This value controls how many "seconds" of
   * movement force are added to the velocity on burst.
   */
  final val BurstSecondsOfMovement = 5f

  def radius(mass: Float): Float = {
    4f + sqrt(mass).toFloat * 3f
  }
}

class Cell(val id: Int, player: Player, var position: Vector2 = new Vector2(0f, 0f)) extends Entity {
  var velocity = new Vector2(0f, 0f)
  var target = position
  var aiState: AIState = defineAiState
  _mass = Cell.MinMass
  val scoreModification = 0

  var burstActive = false

  /**
   * The maximum speed (length of the velocity) for the cell, in units per
   * second.
   */
  def maxSpeed: Float = {
    max(Cell.MaxMaximumSpeed - mass*Cell.SpeedLimitReductionPerMassUnit,
      Cell.MinMaximumSpeed)
  }

  override def mass_=(m: Float): Unit = {
    _mass = max(m, Cell.MinMass)
  }

  def radius: Float = {
    Cell.radius(mass)
  }

  def update(deltaSeconds: Float, grid: Grid): Unit = {
    mass = decayedMass(deltaSeconds)

    target = aiState.update(deltaSeconds, grid)

    position += velocity * deltaSeconds
    keepInGrid(grid)

    velocity += movement(deltaSeconds)
    if (burstActive) applyBurst(deltaSeconds)
    velocity += drag(deltaSeconds)
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

  def drag(deltaSeconds: Float): Vector2 = {
    val overspeed = velocity.magnitude - maxSpeed
    if (overspeed <= 0) {
      return Vector2(0f, 0f)
    }

    -velocity.normalize * overspeed *
      pow(Cell.ExcessVelocityReductionPerSec, deltaSeconds).toFloat
  }

  /**
   * Returns a steering force (unbounded) towards the target velocity
   */
  def steering: Vector2 = {
    val dir = target - position
    val targetVelocity = dir.safeNormalize * maxSpeed
    targetVelocity - velocity
  }

  def movement(deltaSeconds: Float): Vector2 = {
    steering.truncate(maxSpeed) * deltaSeconds
  }

  def performAction(action: Action): Option[ScoreModification] = {
    target = action.target.toVector

    if (action.split) split
    if (action.burst) burst
    val modifications = tradeMass(action.trade)
    modifications
  }

  def burst(): Unit = {
    if (mass < Cell.MinMass + Cell.BurstMassCost) return

    mass -= Cell.BurstMassCost
    burstActive = true
  }

  def applyBurst(deltaSeconds: Float): Unit = {
    velocity += movement(Cell.BurstSecondsOfMovement)
    burstActive = false
  }

  def split(): List[Cell] = {
    if (mass < 2f * Cell.MinMass || player.cells.length >= Player.MaxCells) {
      return List(this)
    }

    val other = player.spawnCell(position)
    other.target = target
    other.mass = mass / 2f
    mass /= 2f

    other.position += Vector2(radius * 2f, radius * 2f) // TODO replace this with a pushing force

    List(this, other)
  }

  def tradeMass(massToTrade: Int): Option[ScoreModification] = {

    val amount = min(massToTrade, max(mass - Cell.MinMass, 0)).toInt

    if (amount > 0 && mass - amount >= Cell.MinMass) {
      mass -= amount
      return Some(ScoreModification(player.id, amount * Cell.MassToScoreRatio))
    }

    None
  }

  def defineAiState: AIState = {
    if (player.isActive()) new NullState(this)
    else new SleepingState(this)
  }

  def state: serializable.Cell = {
    serializable.Cell(id,
                      round(mass),
                      round(radius),
                      position.state,
                      target.state)
  }
}
