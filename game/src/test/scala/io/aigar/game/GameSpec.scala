import io.aigar.game._
import org.scalatest._
import com.github.jpbetz.subspace._

class GameSpec extends FlatSpec with Matchers {
  "A Game" should "generate a new state object every time (thread-safety)" in {
    val game = new Game(42, 10)
    val state1 = game.state
    game.state should not be theSameInstanceAs(state1)
  }

  it should "update its tick count" in {
    val game = new Game(42, 10)
    game.tick should equal(0)

    game.update(1f)

    game.tick should equal(1)
  }

  it should "create just enough players" in {
    val game = new Game(42, 10)

    game.players should have size 10
  }

  it should "spawn players at distinct positions at creation" in {
    val game = new Game(42, 10)

    val positions = game.players.map { _.cells.head.position }

    // This is a soft constraint. We're not enforcing distinct positions, but
    // we assume here that it is unlikely enough to ignore the possibility of
    // duplicates. We are mainly checking that we're not spawning all the
    // players at the same place.
    positions.toSet should have size positions.length
  }

  it should "create players with distinct IDs" in {
    val game = new Game(42, 10)

    val ids = game.players.map { _.id }

    ids.toSet should have size ids.length
  }

  it should "update its players" in {
    val game = new Game(42, 10)
    val player = game.players.head
    val cell = player.cells.head
    cell.position = Vector2(0, 0)
    cell.target = new Vector2(100f, 100f)

    val initialDistance = cell.position.distanceTo(cell.target)

    game.update(1f)

    val finalDistance = cell.position.distanceTo(cell.target)

    initialDistance should be > finalDistance
  }

  it should "create a bigger grid if there are more players" in {
    val game1 = new Game(42, 1)
    val game2 = new Game(42, 10)

    game2.grid.width should be > game1.grid.width
    game2.grid.height should be > game1.grid.height
  }

  it should "create a state with the right info" in {
    val game = new Game(42, 10)

    val state = game.state

    state.players should have size 10
    //TODO add more tests as the rest gets implemented
  }
}
