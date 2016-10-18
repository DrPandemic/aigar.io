import io.aigar.game._
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
}


