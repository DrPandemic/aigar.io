import io.aigar.game._
import scala.math._
import org.scalatest._
import com.github.jpbetz.subspace._

class AIStateSpec extends FlatSpec with Matchers {
  "NullState" should "return the same instance as the cell's target" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    cell.target = Vector2(10f, 10f)
    player.aiState = new NullState(player)

    val target = player.aiState.update(1f, new Grid(0, 0), cell)

    target should be theSameInstanceAs(cell.target)
  }

  it should "switch to a wandering state after inactivity for too long" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    player.aiState = new NullState(player)

    player.aiState.update(NullState.MaxInactivitySeconds + 1e-2f, new Grid(0, 0), cell)

    player.aiState shouldBe a [WanderingState]
  }

  it should "not switch to a wandering state after activity" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    player.aiState = new NullState(player)

    player.aiState.update(NullState.MaxInactivitySeconds - 1e-2f, new Grid(0, 0), cell)
    player.aiState.onPlayerActivity
    player.aiState.update(NullState.MaxInactivitySeconds - 1e-2f, new Grid(0, 0), cell)

    player.aiState shouldBe a [NullState]
  }

  "SeekingState" should "keep the current cell's target on creation" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    cell.target = Vector2(100f, 100f)
    player.aiState = new SeekingState(player)

    val target = player.aiState.update(1f, new Grid(0, 0), cell)

    target should be theSameInstanceAs(cell.target)
  }

  it should "change state to SleepingState when it reaches its target" in {
    val player = new Player(0, Vector2(10f, 10f))
    val cell = player.cells.head
    cell.target = Vector2(10f, 10f)
    player.aiState = new SeekingState(player)

    player.aiState.update(1f, new Grid(0, 0), cell)

    player.aiState shouldBe a [SleepingState]
  }

  it should "change state to SleepingState after a certain delay" in {
    val player = new Player(0, Vector2(0f, 0f))
    val cell = player.cells.head
    val targetFar = Vector2(100000f, 100000f)
    cell.target = targetFar
    player.aiState = new SeekingState(player)

    val target = player.aiState.update(WanderingState.NewTargetDelay + 1e-2f, new Grid(0, 0), cell)

    cell.position.distanceTo(targetFar) should be > 100f // make sure that we really changed because of time (not position)
    player.aiState shouldBe a [SleepingState]
  }

  it should "change to null state on player activity" in {
    val player = new Player(0, Vector2(5f, 5f))
    val cell = player.cells.head
    player.aiState = new SeekingState(player)

    player.aiState.onPlayerActivity

    player.aiState shouldBe a [NullState]
  }
}
