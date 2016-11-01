import io.aigar.game._
import scala.math._
import org.scalatest._
import com.github.jpbetz.subspace._

class AIStateSpec extends FlatSpec with Matchers {
  "NullState" should "return the same instance as the cell's target" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    cell.target = Vector2(10f, 10f)
    player.machineState = new NullState(player)

    val target = player.machineState.update(1f, new Grid(0, 0), cell)

    target should be theSameInstanceAs(cell.target)
  }

  it should "switch to a wandering state after inactivity for too long" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    player.machineState = new NullState(player)

    player.machineState.update(NullState.MaxInactivitySeconds + 1e-2f, new Grid(0, 0), cell)

    player.machineState shouldBe a [WanderingState]
  }

  it should "not switch to a wandering state after activity" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    player.machineState = new NullState(player)

    player.machineState.update(NullState.MaxInactivitySeconds - 1e-2f, new Grid(0, 0), cell)
    player.machineState.onPlayerActivity
    player.machineState.update(NullState.MaxInactivitySeconds - 1e-2f, new Grid(0, 0), cell)

    player.machineState shouldBe a [NullState]
  }

  "WanderingState" should "keep the current cell's target on creation" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    cell.target = Vector2(10f, 10f)
    player.machineState = new WanderingState(player)

    val target = player.machineState.update(1f, new Grid(0, 0), cell)

    target should be theSameInstanceAs(cell.target)
  }

  it should "change target when it reaches its target" in {
    val player = new Player(0, Vector2(10f, 10f))
    val cell = player.cells.head
    cell.target = Vector2(10f, 10f)
    player.machineState = new WanderingState(player)

    val target = player.machineState.update(1f, new Grid(0, 0), cell)

    target should not be theSameInstanceAs(cell.target)
  }

  it should "change target after a certain delay" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    val targetFar = Vector2(100000f, 100000f)
    cell.target = targetFar
    player.machineState = new WanderingState(player)

    val target = player.machineState.update(WanderingState.NewTargetDelay + 1e-2f, new Grid(0, 0), cell)

    cell.position.distanceTo(targetFar) should be > 100f // make sure that we really changed because of time (not position)
    target should not be theSameInstanceAs(cell.target)
  }

  it should "change to null state on player activity" in {
    val player = new Player(0, Vector2(5f, 5f))
    val cell = player.cells.head
    player.machineState = new WanderingState(player)

    player.machineState.onPlayerActivity

    player.machineState shouldBe a [NullState]
  }
}
