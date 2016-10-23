import io.aigar.game._
import scala.math._
import org.scalatest._
import com.github.jpbetz.subspace._

class SteeringBehaviorSpec extends FlatSpec with Matchers {
  "NoBehavior" should "return the same instance as the cell's target" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.target = Vector2(10f, 10f)
    cell.behavior = new NoBehavior(cell)

    val target = cell.behavior.update(1f, new Grid(0, 0))

    target should be theSameInstanceAs(cell.target)
  }

  it should "switch to a wandering behavior after inactivity for too long" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.behavior = new NoBehavior(cell)

    cell.behavior.update(NoBehavior.MaxInactivitySeconds + 1e-2f, new Grid(0, 0))

    cell.behavior shouldBe a [WanderingBehavior]
  }

  it should "not switch to a wandering behavior after activity" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.behavior = new NoBehavior(cell)

    cell.behavior.update(NoBehavior.MaxInactivitySeconds - 1e-2f, new Grid(0, 0))
    cell.behavior.onPlayerActivity
    cell.behavior.update(NoBehavior.MaxInactivitySeconds - 1e-2f, new Grid(0, 0))

    cell.behavior shouldBe a [NoBehavior]
  }




  "WanderingBehavior" should "keep the current cell's target on creation" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.target = Vector2(10f, 10f)
    cell.behavior = new WanderingBehavior(cell)

    val target = cell.behavior.update(1f, new Grid(0, 0))

    target should be theSameInstanceAs(cell.target)
  }

  it should "change target when close enough to the current target" in {
    val cell = new Cell(1, Vector2(10f, 10f))
    cell.target = Vector2(10f, 10f)
    cell.behavior = new WanderingBehavior(cell)
    
    val target = cell.behavior.update(1f, new Grid(0, 0))

    target should not be theSameInstanceAs(cell.target)
  }

  it should "change to no behavior on player activity" in {
    val cell = new Cell(1, Vector2(5f, 5f))
    cell.behavior = new WanderingBehavior(cell)

    cell.behavior.onPlayerActivity

    cell.behavior shouldBe a [NoBehavior]
  }
}
