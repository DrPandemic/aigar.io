import io.aigar.game._
import scala.math._
import org.scalatest._
import com.github.jpbetz.subspace._

class AIStateSpec extends FlatSpec with Matchers {
  "NullState" should "return the same instance as the cell's target" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    cell.target = Vector2(10f, 10f)
    cell.aiState = new NullState(cell)

    val target = cell.aiState.update(1f, new Grid(0, 0))

    target should be theSameInstanceAs(cell.target)
  }

  it should "switch to a wandering state after inactivity for too long" in {
    val player = new Player(0, Vector2(0f, 0f))
    player.active = true
    val cell = player.cells.head
    cell.aiState = new NullState(cell)

    cell.aiState.update(NullState.MaxInactivitySeconds + 1e-2f, new Grid(0, 0))

    cell.aiState shouldBe a [WanderingState]
  }

  it should "not switch to a wandering state after activity" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    cell.aiState = new NullState(cell)

    cell.aiState.update(NullState.MaxInactivitySeconds - 1e-2f, new Grid(0, 0))
    cell.aiState.onPlayerActivity
    cell.aiState.update(NullState.MaxInactivitySeconds - 1e-2f, new Grid(0, 0))

    cell.aiState shouldBe a [NullState]
  }

  "SeekingState" should "keep the current cell's target on creation" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    cell.target = Vector2(100f, 100f)
    cell.aiState = new SeekingState(cell)

    val target = cell.aiState.update(1f, new Grid(0, 0))

    target should be theSameInstanceAs(cell.target)
  }

  it should "change state to SleepingState when it reaches its target" in {
    val player = new Player(0, Vector2(10f, 10f))
    val cell = player.cells.head
    cell.target = Vector2(10f, 10f)
    cell.aiState = new SeekingState(cell)

    cell.aiState.update(1f, new Grid(0, 0))

    cell.aiState shouldBe a [SleepingState]
  }

  it should "change state to SleepingState after a certain delay" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    val targetFar = Vector2(100000f, 100000f)
    cell.target = targetFar
    cell.aiState = new SeekingState(cell)

    val target = cell.aiState.update(WanderingState.NewTargetDelay + 1e-2f, new Grid(0, 0))

    cell.position.distanceTo(targetFar) should be > 100f // make sure that we really changed because of time (not position)
    cell.aiState shouldBe a [SleepingState]
  }

  it should "change to null state on player activity" in {
    val player = new Player(0, Vector2(5f, 5f))
    player.active = false
    val cell = player.cells.head
    cell.aiState = new SeekingState(cell)

    cell.aiState.onPlayerActivity

    cell.aiState shouldBe a [NullState]
  }

  "SleepingBehavior" should "returns the cells's position as the new target" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    cell.target = Vector2(100f, 100f)
    cell.aiState = new SleepingState(cell)

    val target = cell.aiState.update(1f, new Grid(0, 0))

    target should be theSameInstanceAs(cell.position)
  }

  it should "change cell's state to SeekingState after a certain delay" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    cell.target = Vector2(100f, 100f)
    cell.aiState = new SleepingState(cell)

    cell.aiState.update(WanderingState.SleepingDelay + 1e-2f, new Grid(0, 0))

    cell.aiState shouldBe a [SeekingState]
  }

  it should "change the cell's target after a certain delay" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    cell.target = Vector2(100f, 100f)
    cell.aiState = new SleepingState(cell)

    val target = cell.aiState.update(WanderingState.SleepingDelay + 1e-2f, new Grid(0, 0))

    target shouldNot be theSameInstanceAs(cell.target)
  }
}
