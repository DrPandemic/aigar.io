package io.aigar.game

import scala.util.Random
import scala.math.{cos, sin, Pi}
import com.github.jpbetz.subspace._

/**
 * Represents a movement behavior of an entity.
 * This is used to make a cell move on its own (server AI).
 */
trait SteeringBehavior {
  /**
   * Determines what the next target of a cell should be.
   */
  def update(deltaSeconds: Float): Vector2
  def onPlayerActivity: Unit
  def isActive: Boolean
}

class WanderingBehavior(cell: Cell) extends SteeringBehavior {
  val random = new Random
  var wanderAngle = 0f

  def isActive = false

  /**
   * Picks a target that gives an illusion of "wandering around".
   *
   * The way that this behavior is implemented is that we imagine a "circle" in
   * front of the entity and place our target on that circle. As we update the
   * behavior, it randomly moves the target a bit along the circle.
   * See: https://gamedevelopment.tutsplus.com/tutorials/understanding-steering-behaviors-wander--gamedev-1624
   */
  def update(deltaSeconds: Float) = {
    val target = circleCenter + displacementOnCircle

    val multiplier = random.nextFloat() * 0.5f
    wanderAngle += multiplier * WanderingBehavior.AngleChange * deltaSeconds

    target
  }

  def onPlayerActivity {
    cell.behavior = new NoBehavior(cell)
  }

  def circleCenter = {
    val diff = cell.target - cell.position
    val dir = if (diff.magnitude > 0) diff.normalize else Vector2(0f, -1f)
    cell.position + dir * WanderingBehavior.CircleDistance
  }

  def displacementOnCircle = {
    Vector2(cos(wanderAngle).toFloat, sin(wanderAngle).toFloat) * WanderingBehavior.CircleRadius
  }
}
object WanderingBehavior {
  // how far away the circle in front of the cell should be
  final val CircleDistance = 50f

  // how wide the circle in front of the cell should be
  final val CircleRadius = 10f

  // how much (radians) the wandering angle can shift per second
  final val AngleChange = Pi.toFloat / 4f
}

/**
 * Returns the original cell target on update (does nothing).
 *
 * Use this when an entity is not controlled by the server.
 */
class NoBehavior(cell: Cell) extends SteeringBehavior {
  var inactivityTimeLeft = NoBehavior.MaxInactivitySeconds

  def isActive = true

  def update(deltaSeconds: Float) = {
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
  def isActive = false
  def update(deltaSeconds: Float) = {
    updated = true
    new Vector2(0f, 0f)
  }
  def onPlayerActivity {
    active = true
  }
}
