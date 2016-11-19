package io.aigar.game

import com.github.jpbetz.subspace.Vector2

/**
 * Represents an entity's state.
 * This is used to make a cell move on its own (server AI).
 */
trait AIState {
  /**
   * Determines what the next target of a cell should be.
   */
  def update(deltaSeconds: Float, grid: Grid, cell: Cell): Vector2
  def onPlayerActivity: Unit
  def isActive: Boolean
}

abstract class WanderingState(player: Player) extends AIState {
  def isActive = false

  def onPlayerActivity: Unit = {
    player.aiState = new NullState(player)
  }
}

class SeekingState(player: Player) extends WanderingState(player) {
  var wanderingTimeLeft = WanderingState.NewTargetDelay

  /**
    * Picks a random target. Go in sleep mode once the previous one is reached.
    */
  def update(deltaSeconds: Float, grid: Grid, cell: Cell): Vector2 = {
    wanderingTimeLeft -= deltaSeconds

    if (cell.contains(cell.target) || wanderingTimeLeft <= 0f) {
      player.aiState = new SleepingState(player)
    }

    cell.target
  }
}

class SleepingState(player: Player) extends WanderingState(player) {
  var sleepingTimeLeft = WanderingState.SleepingDelay

  /**
    * Picks a random target. Go in sleep mode once the previous one is reached.
    */
  def update(deltaSeconds: Float, grid: Grid, cell: Cell): Vector2 = {
    sleepingTimeLeft -= deltaSeconds

    if (sleepingTimeLeft <= 0f) {
      player.aiState = new SeekingState(player)
      grid.randomPosition
    } else {
      cell.position
    }
  }
}

object WanderingState {
  // how long we have to wait to pick a new target (seconds)
  final val NewTargetDelay = 60f
  final val SleepingDelay = 10f
}

/**
 * Returns the original cell target on update (does nothing).
 *
 * Use this when an entity is not controlled by the server.
 */
class NullState(player: Player) extends AIState {
  var inactivityTimeLeft = NullState.MaxInactivitySeconds

  def isActive = true

  def update(deltaSeconds: Float, grid: Grid, cell: Cell): Vector2 = {
    inactivityTimeLeft -= deltaSeconds
    if (inactivityTimeLeft < 0f) {
      player.aiState = new SleepingState(player)
    }

    cell.target
  }

  def onPlayerActivity: Unit = {
    inactivityTimeLeft = NullState.MaxInactivitySeconds
  }
}
object NullState {
  final val MaxInactivitySeconds = 2f
}


/**
 * Spy state used for testing purposes.
 */
class TestState extends AIState {
  var updated = false
  var active = false

  def update(deltaSeconds: Float, grid: Grid, cell: Cell): Vector2 = {
    updated = true
    new Vector2(0f, 0f)
  }

  def onPlayerActivity: Unit = {
    active = true
  }

  def isActive = false
}
