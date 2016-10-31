package io.aigar.game

import com.github.jpbetz.subspace._

/**
 * Represents a state of an entity.
 * This is used to make a cell move on its own (server AI).
 */
trait AIState {
  /**
   * Determines what the next target of a cell should be.
   */
  def update(deltaSeconds: Float, grid: Grid): Vector2
  def onPlayerActivity: Unit
  def isActive: Boolean
}

class WanderingState(cell: Cell) extends AIState {
  def isActive = false

  var nextTargetTimeLeft = WanderingState.NewTargetDelay

  /**
   * Picks a random target. Picks a new one once the previous one is reached.
   */
  def update(deltaSeconds: Float, grid: Grid) = {
    nextTargetTimeLeft -= deltaSeconds

    if (cell.contains(cell.target) || nextTargetTimeLeft <= 0f) {
      nextTargetTimeLeft = WanderingState.NewTargetDelay
      grid.randomPosition
    } else {
      cell.target
    }
  }

  def onPlayerActivity {
    cell.machineState = new NullState(cell)
  }
}
object WanderingState {
  // how long we have to wait to pick a new target (seconds)
  final val NewTargetDelay = 10f
}

/**
 * Returns the original cell target on update (does nothing).
 *
 * Use this when an entity is not controlled by the server.
 */
class NullState(cell: Cell) extends AIState {
  var inactivityTimeLeft = NullState.MaxInactivitySeconds

  def isActive = true

  def update(deltaSeconds: Float, grid: Grid) = {
    inactivityTimeLeft -= deltaSeconds
    if (inactivityTimeLeft < 0f) {
      cell.machineState = new WanderingState(cell)
    }

    cell.target
  }

  def onPlayerActivity = {
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

  def update(deltaSeconds: Float, grid: Grid) = {
    updated = true
    new Vector2(0f, 0f)
  }
  def onPlayerActivity {
    active = true
  }

  def isActive = false
}
