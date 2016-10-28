package io.aigar.game

import io.aigar.controller.response.Action
import io.aigar.game.Position2Utils._
import com.github.jpbetz.subspace._

/**
 * Represents a movement behavior of an entity.
 * This is used to make a cell move on its own (server AI).
 */
trait SteeringBehavior {
  /**
   * Determines what the next target of a cell should be.
   */
  def update(deltaSeconds: Float, grid: Grid): Vector2
  def onPlayerActivity: Unit
  def isActive: Boolean
}

class WanderingBehavior(cell: Cell) extends SteeringBehavior {
  def isActive = false

  var nextTargetTimeLeft = WanderingBehavior.NewTargetDelay

  /**
   * Picks a random target. Picks a new one once the previous one is reached.
   */
  def update(deltaSeconds: Float, grid: Grid) = {
    nextTargetTimeLeft -= deltaSeconds

    if (cell.contains(cell.target) || nextTargetTimeLeft <= 0f) {
      nextTargetTimeLeft = WanderingBehavior.NewTargetDelay
      grid.randomPosition
    } else {
      cell.target
    }
  }

  def onPlayerActivity {
    cell.behavior = new NoBehavior(cell)
  }
}
object WanderingBehavior {
  // how long we have to wait to pick a new target (seconds)
  final val NewTargetDelay = 10f
}

/**
 * Returns the original cell target on update (does nothing).
 *
 * Use this when an entity is not controlled by the server.
 */
class NoBehavior(cell: Cell) extends SteeringBehavior {
  var inactivityTimeLeft = NoBehavior.MaxInactivitySeconds

  def isActive = true

  def update(deltaSeconds: Float, grid: Grid) = {
    inactivityTimeLeft -= deltaSeconds
    if (inactivityTimeLeft < 0f) {
      cell.behavior = new WanderingBehavior(cell)
    }

    cell.target
  }

  def onPlayerActivity = {
    inactivityTimeLeft = NoBehavior.MaxInactivitySeconds
  }
}
object NoBehavior {
  final val MaxInactivitySeconds = 2f
}


/**
 * Spy behavior used for testing purposes.
 */
class TestBehavior extends SteeringBehavior {
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
