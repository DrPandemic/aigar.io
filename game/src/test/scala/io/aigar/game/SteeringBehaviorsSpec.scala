import io.aigar.game._
import io.aigar.game.serializable.Position
import io.aigar.game.Vector2Utils._
import io.aigar.controller.response.Action
import org.scalatest._
import com.github.jpbetz.subspace._

class SteeringBehaviorSpec extends FlatSpec with Matchers {
  "NoBehavior" should "return the same instance as the cell's target" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.target = Vector2(10f, 10f)
    cell.behavior = new NoBehavior(cell)

    val target = cell.behavior.update(1f, new Grid(0, 0), None)

    target should be theSameInstanceAs(cell.target)
  }

  "NoBehavior" should "return an updated target" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.target = Vector2(10f, 10f)
    cell.behavior = new NoBehavior(cell)

    cell.behavior.update(1f, new Grid(0, 0), Some(Action(1, false, false, false, 0, Position(42f, 42f))))

    cell.target.state should equal(Position(42f, 42f))
  }

  it should "switch to a wandering behavior after inactivity for too long" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.behavior = new NoBehavior(cell)

    cell.behavior.update(NoBehavior.MaxInactivitySeconds + 1e-2f, new Grid(0, 0), None)

    cell.behavior shouldBe a [WanderingBehavior]
  }

  it should "not switch to a wandering behavior after activity" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.behavior = new NoBehavior(cell)

    cell.behavior.update(NoBehavior.MaxInactivitySeconds - 1e-2f, new Grid(0, 0), None)
    cell.behavior.onPlayerActivity
    cell.behavior.update(NoBehavior.MaxInactivitySeconds - 1e-2f, new Grid(0, 0), None)

    cell.behavior shouldBe a [NoBehavior]
  }


  "WanderingBehavior" should "keep the current cell's target on creation" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.target = Vector2(10f, 10f)
    cell.behavior = new WanderingBehavior(cell)

    val target = cell.behavior.update(1f, new Grid(0, 0), None)

    target should be theSameInstanceAs(cell.target)
  }

  it should "change target when it reaches its target" in {
    val cell = new Cell(1, Vector2(10f, 10f))
    cell.target = Vector2(10f, 10f)
    cell.behavior = new WanderingBehavior(cell)

    val target = cell.behavior.update(1f, new Grid(0, 0), None)

    target should not be theSameInstanceAs(cell.target)
  }

  it should "change target after a certain delay" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    val targetFar = Vector2(100000f, 100000f)
    cell.target = targetFar
    cell.behavior = new WanderingBehavior(cell)

    val target = cell.behavior.update(WanderingBehavior.NewTargetDelay + 1e-2f, new Grid(0, 0), None)

    cell.position.distanceTo(targetFar) should be > 100f // make sure that we really changed because of time (not position)
    target should not be theSameInstanceAs(cell.target)
  }

  it should "change to no behavior on player activity" in {
    val cell = new Cell(1, Vector2(5f, 5f))
    cell.behavior = new WanderingBehavior(cell)

    cell.behavior.onPlayerActivity

    cell.behavior shouldBe a [NoBehavior]
  }
}
