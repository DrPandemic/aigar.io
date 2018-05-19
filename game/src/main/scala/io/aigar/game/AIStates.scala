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
  def update(deltaSeconds: Float, grid: Grid): Vector2
  def onPlayerActivity(): Unit
}

abstract class WanderingState(cell: Cell) extends AIState {
  def onPlayerActivity: Unit = {
    cell.player.active = true
  }
}

class SeekingState(cell: Cell) extends WanderingState(cell) {
  var wanderingTimeLeft = WanderingState.NewTargetDelay

  /**
    * Picks a random target. Go in sleep mode once the previous one is reached.
    */
  def update(deltaSeconds: Float, grid: Grid): Vector2 = {
    wanderingTimeLeft -= deltaSeconds

    if (cell.contains(cell.target) || wanderingTimeLeft <= 0f) {
      cell.aiState = new SleepingState(cell)
    }

    cell.target
  }
}

class SleepingState(cell: Cell) extends WanderingState(cell) {
  var sleepingTimeLeft = WanderingState.SleepingDelay

  /**
    * Picks a random target. Go in sleep mode once the previous one is reached.
    */
  def update(deltaSeconds: Float, grid: Grid): Vector2 = {
    sleepingTimeLeft -= deltaSeconds

    if (sleepingTimeLeft <= 0f) {
      cell.aiState = new SeekingState(cell)
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
class NullState(cell: Cell) extends AIState {
  var inactivityTimeLeft = NullState.MaxInactivitySeconds

  def update(deltaSeconds: Float, grid: Grid): Vector2 = {
    inactivityTimeLeft -= deltaSeconds
    if (inactivityTimeLeft < 0f) {
      cell.player.active = false
    }

    cell.target
  }

  def onPlayerActivity: Unit = {
    inactivityTimeLeft = NullState.MaxInactivitySeconds
  }
}
object NullState {
  final val MaxInactivitySeconds = 5f
}


/**
 * Spy state used for testing purposes.
 */
class TestState(cell: Cell) extends AIState {
  var updated = false
  var active = false

  def update(deltaSeconds: Float, grid: Grid): Vector2 = {
    updated = true
    new Vector2(0f, 0f)
  }

  def onPlayerActivity: Unit = {
    active = true
    cell.player.active = true
  }
}
