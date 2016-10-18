import io.aigar.game._
import scala.math._
import org.scalatest._
import com.github.jpbetz.subspace._

class SteeringBehaviorSpec extends FlatSpec with Matchers {
  "NoBehavior" should "return the same instance as the cell's target" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.target = Vector2(10f, 10f)
    val behavior = new NoBehavior(cell)

    val target = behavior.update(1f)

    target should be theSameInstanceAs(cell.target)
  }

  "WanderingBehavior" should "return a different target from the original one" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.target = Vector2(10f, 10f)
    val behavior = new WanderingBehavior(cell)

    val target = behavior.update(1f)

    target should not be theSameInstanceAs(cell.target)
  }

  it should "successively return different targets (wandering around)" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.target = Vector2(10f, 10f)
    val behavior = new WanderingBehavior(cell)
    
    val before = behavior.update(1f)
    val after = behavior.update(1f)

    val distance = before.distanceTo(after)

    // Slight possibility that we get a random angle change of 0 (no change)
    // We accept this rare event here.
    distance should be > 0f

    // Specific to wandering behavior: target should be within the circle ahead
    // of our cell
    distance should be <= 2 * WanderingBehavior.CircleRadius
  }

  it should "return a circle center ahead of the cell" in {
    val cell = new Cell(1, Vector2(5f, 5f))
    cell.target = Vector2(10f, 5f)
    val behavior = new WanderingBehavior(cell)

    behavior.circleCenter should equal(Vector2(5f + WanderingBehavior.CircleDistance, 5f))
  }

  it should "return a displacement along the circle based on its wander angle" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    val behavior = new WanderingBehavior(cell)
    behavior.wanderAngle = Pi.toFloat

    val displacement = behavior.displacementOnCircle

    displacement.x should equal(-WanderingBehavior.CircleRadius +- 1e-5f)
    displacement.y should equal(0f +- 1e-5f)
  }
}


