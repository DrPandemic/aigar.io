import io.aigar.controller.response.Action
import io.aigar.game._
import io.aigar.score.ScoreModification
import io.aigar.game.serializable.Position
import org.scalatest._
import com.github.jpbetz.subspace._

class GameSpec extends FlatSpec with Matchers {
  "A Game" should "generate a new state object every time (thread-safety)" in {
    val game = new Game(42, List())
    val state1 = game.state
    game.state should not be theSameInstanceAs(state1)
  }

  it should "update its tick count" in {
    val game = new Game(42, List())
    game.tick should equal(0)

    game.update(1f)

    game.tick should equal(1)
  }

  it should "create just enough players" in {
    val game = new Game(42, 1 to 10 toList)

    game.players should have size 10
  }

  it should "spawn players at distinct positions at creation" in {
    val game = new Game(42, 1 to 10 toList)

    val positions = game.players.map { _.cells.head.position }

    // This is a soft constraint. We're not enforcing distinct positions, but
    // we assume here that it is unlikely enough to ignore the possibility of
    // duplicates. We are mainly checking that we're not spawning all the
    // players at the same place.
    positions.toSet should have size positions.length
  }

  it should "update its players" in {
    val game = new Game(42, List(1))
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
    val game1 = new Game(42, List(1))
    val game2 = new Game(42, 1 to 10 toList)

    game2.grid.width should be > game1.grid.width
    game2.grid.height should be > game1.grid.height
  }

  it should "create a state with the right info" in {
    val game = new Game(42, 1 to 10 toList)

    val state = game.state

    state.players should have size 10
    //TODO add more tests as the rest gets implemented
  }

  "update" should "return a list of ScoreModification" in {
    val game = new Game(42, List(0))
    game.resources.regular.positions = List(Vector2(40, 0))
    val player = game.players.head
    player.cells.head.position = Vector2(40, 0)
    player.cells.head.target = Vector2(40, 0)
    player.machineState = new NullState(player)

    val resourceModifications = game.update(0f)

    resourceModifications should contain (ScoreModification(player.id, Regular.Score))
  }

  "performAction" should "update cell's targets" in {
    val game = new Game(0, List(1, 2, 3))
    game.performAction(1, List(Action(0, false, false, false, 0, Position(0f, 10f))))
    game.performAction(2, List(Action(0, false, false, false, 0, Position(20f, 10f))))
    game.performAction(3, List(Action(0, false, false, false, 0, Position(50f, 1f))))

    val state = game.state
    val p1 = state.players.find(_.id == 1).get
    val p2 = state.players.find(_.id == 2).get
    val p3 = state.players.find(_.id == 3).get

    p1.cells.find(_.id == 0).get.target should equal(Position(0f, 10f))
    p2.cells.find(_.id == 0).get.target should equal(Position(20f, 10f))
    p3.cells.find(_.id == 0).get.target should equal(Position(50f, 1f))
  }
}
