package io.aigar.game

import io.aigar.controller.response.Action
import io.aigar.score.ScoreModification
import io.aigar.game.Vector2Utils.Vector2Addons
import io.aigar.game.Position2Utils.PositionAddon
import com.github.jpbetz.subspace.Vector2
import scala.math.{log, max, min, sqrt, round, pow}

object Cell {
  /**
   * Default mass of a cell (at spawn).
   *
   * IMPORTANT must stay in sync with the client's documentation
   */
  final val MinMass = 20f

  /**
    * The cell can't go above this limit.
    */
  final val MaxMass = 10000f

  /**
   * Ratio of mass lost per second.
   */
  final val MassDecayPerSecond = 0.003f

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

  final val MinMaximumSpeed = 50f
  final val MaxMaximumSpeed = 100f
  final val SpeedLimitReductionPerMassUnit = 0.05f

  final val RespawnRetryAttempts = 15

  /**
   * When the velocity of a cell goes above its "max speed", a "drag" force is
   * simulated to remove a ratio of the velocity excess. This constant controls
   * what ratio is removed from the excess, per second.
   */
  final val ExcessVelocityReductionPerSec = 0.25f

  /**
   * How much mass is required to gain a small burst of movement for a short
   * amount of time.
   *
   * IMPORTANT keep this value in sync with the client documentation
   */
  final val BurstMassPercentCost = 0.04f

  /**
   * Bursting works by increasing the max speed of a cell for a certain amount
   * of time. This value controls how many seconds of additional burst
   * (increased speed) the cell gets.
   */
  final val BurstDurationSeconds = 0.25f

  /**
   * By how much we should multiply the max speed of a cell while it is
   * bursting.
   */
  final val BurstMaxSpeedMultiplier = 3f

  /**
   * How long the cell stays in a "sleeping" state after a trade, during which
   * it can't control its movements or send new actions.
   */
  final val TradeSleepingDurationSeconds = 5f

  final val SplitPushSecondsOfMovement = 10f

  def radius(mass: Float): Float = {
    4f + sqrt(mass).toFloat * 3f
  }
}

class Cell(val id: Int, val player: Player, var position: Vector2 = new Vector2(0f, 0f)) extends Entity {
  var velocity = new Vector2(0f, 0f)
  var target = position
  var aiState: AIState = defineAiState
  _mass = Cell.MinMass

  var burstTimeRemaining = 0f
  var sleepingTimeRemaining = 0f
  var massTraded = 0

  /**
   * The maximum speed (length of the velocity) for the cell, in units per
   * second.
   */
  def maxSpeed: Float = {
    val multiplier = if (isBursting) Cell.BurstMaxSpeedMultiplier else 1f
    max(Cell.MaxMaximumSpeed - mass*Cell.SpeedLimitReductionPerMassUnit,
      Cell.MinMaximumSpeed) * multiplier
  }

  override def mass_=(m: Float): Unit = {
    _mass = min(max(m, Cell.MinMass), Cell.MaxMass)
  }

  def radius: Float = {
    Cell.radius(mass)
  }

  override def scoreModification(): Float = {
    (1 + log(mass) - log(Cell.MinMass)).toFloat
  }

  def update(deltaSeconds: Float, grid: Grid): Option[ScoreModification] = {
    mass = decayedMass(deltaSeconds)

    target = aiState.update(deltaSeconds, grid)
    if (isSleeping) target = position

    position += velocity * deltaSeconds
    keepInGrid(grid)

    velocity += movement(deltaSeconds)
    velocity += drag(deltaSeconds)
    burstTimeRemaining = max(burstTimeRemaining - deltaSeconds, 0f)
    updateTrade(deltaSeconds)
  }

  def updateTrade(deltaSeconds: Float): Option[ScoreModification] = {
    if (sleepingTimeRemaining > 0f) {
      sleepingTimeRemaining -= deltaSeconds
      if (sleepingTimeRemaining <= 0f) {  // trade complete
        sleepingTimeRemaining = 0f
        val modification = ScoreModification(player.id, massTraded * Cell.MassToScoreRatio)
        massTraded = 0
        return Some(modification)
      }
    }
    None
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
    if (isSleeping) return None
    target = action.target.toVector

    if (action.split) split
    if (action.burst) burst
    if (action.trade > 0) tradeMass(action.trade)
    None
  }

  def burst(): Unit = {
    val burstCost = mass * Cell.BurstMassPercentCost
    if (mass < Cell.MinMass + burstCost) return

    mass -= burstCost
    burstTimeRemaining += Cell.BurstDurationSeconds
  }

  def isBursting(): Boolean = {
    burstTimeRemaining > 0f
  }

  def isSleeping(): Boolean = {
    sleepingTimeRemaining > 0f
  }

  def split(): List[Cell] = {
    if (mass < 2f * Cell.MinMass || player.cells.length >= Player.MaxCells) {
      return List(this)
    }

    val other = player.spawnCell(position)
    other.target = target
    other.mass = mass / 2f
    mass /= 2f

    applySplitPushBack(other)

    List(this, other)
  }

  def applySplitPushBack(other: Cell) {
    var direction = velocity.safeNormalize
    if (direction.magnitude == 0f) {
      direction = Vector2Utils.randomUnitVector
    }
    val pushDirection = direction.perpendicular

    // position so that they don't overlap
    position += pushDirection * radius
    other.position -= pushDirection * radius

    val pushForce = pushDirection * maxSpeed * Cell.SplitPushSecondsOfMovement

    other.velocity = velocity
    velocity += pushForce
    other.velocity -= pushForce
  }

  def tradeMass(massToTrade: Int): Unit = {
    val amount = min(massToTrade, max(mass - Cell.MinMass, 0)).toInt

    if (amount > 0 && mass - amount >= Cell.MinMass) {
      mass -= amount
      massTraded = amount
      sleepingTimeRemaining = Cell.TradeSleepingDurationSeconds
    }
  }

  def defineAiState: AIState = {
    if (player.active) new NullState(this)
    else new SleepingState(this)
  }

  def state: serializable.Cell = {
    serializable.Cell(id,
                      round(mass),
                      round(radius),
                      position.state,
                      target.state,
                      isBursting)
  }
}
