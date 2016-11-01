import io.aigar.game._
import scala.math._
import org.scalatest._
import com.github.jpbetz.subspace._

class AIStateSpec extends FlatSpec with Matchers {
  "NullState" should "return the same instance as the cell's target" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.target = Vector2(10f, 10f)
    cell.machineState = new NullState(cell)

    val target = cell.machineState.update(1f, new Grid(0, 0))

    target should be theSameInstanceAs(cell.target)
  }

  it should "switch to a wandering state after inactivity for too long" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.machineState = new NullState(cell)

    cell.machineState.update(NullState.MaxInactivitySeconds + 1e-2f, new Grid(0, 0))

    cell.machineState shouldBe a [WanderingState]
  }

  it should "not switch to a wandering state after activity" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.machineState = new NullState(cell)

    cell.machineState.update(NullState.MaxInactivitySeconds - 1e-2f, new Grid(0, 0))
    cell.machineState.onPlayerActivity
    cell.machineState.update(NullState.MaxInactivitySeconds - 1e-2f, new Grid(0, 0))

    cell.machineState shouldBe a [NullState]
  }




  "WanderingState" should "keep the current cell's target on creation" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    cell.target = Vector2(10f, 10f)
    cell.machineState = new WanderingState(cell)

    val target = cell.machineState.update(1f, new Grid(0, 0))

    target should be theSameInstanceAs(cell.target)
  }

  it should "change target when it reaches its target" in {
    val cell = new Cell(1, Vector2(10f, 10f))
    cell.target = Vector2(10f, 10f)
    cell.machineState = new WanderingState(cell)

    val target = cell.machineState.update(1f, new Grid(0, 0))

    target should not be theSameInstanceAs(cell.target)
  }

  it should "change target after a certain delay" in {
    val cell = new Cell(1, Vector2(0f, 0f))
    val targetFar = Vector2(100000f, 100000f)
    cell.target = targetFar
    cell.machineState = new WanderingState(cell)

    val target = cell.machineState.update(WanderingState.NewTargetDelay + 1e-2f, new Grid(0, 0))

    cell.position.distanceTo(targetFar) should be > 100f // make sure that we really changed because of time (not position)
    target should not be theSameInstanceAs(cell.target)
  }

  it should "change to null state on player activity" in {
    val cell = new Cell(1, Vector2(5f, 5f))
    cell.machineState = new WanderingState(cell)

    cell.machineState.onPlayerActivity

    cell.machineState shouldBe a [NullState]
  }
}
