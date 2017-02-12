import io.aigar.controller.response.Action
import io.aigar.game.{Game, NullState, Regular, Resource, Virus}
import io.aigar.game.serializable.Position
import io.aigar.score.ScoreModification
import scala.concurrent.duration._
import scala.concurrent.Await
import org.scalatest.{FlatSpec, Matchers}
import com.github.jpbetz.subspace.Vector2

class GameSpec extends FlatSpec with Matchers {
  // This shouldn't slow test. It will only take this long when there's an issue with the test
  val AwaitTime = 1 seconds

  "A Game" should "generate a new state object every time (thread-safety)" in {
    val game = new Game(42, List())
    val state1 = game.state
    game.state should not be theSameInstanceAs(state1)
  }

  it should "update its tick count" in {
    val game = new Game(42, List())
    game.tick should equal(0)

    Await.result(game.update, AwaitTime)

    game.tick should equal(1)
  }

  it should "create just enough players" in {
    val game = new Game(42, 1 to 10 toList)

    game.players should have size 10
  }

  it should "create just enough viruses" in {
    val players = 1 to 10 toList
    val game = new Game(42, players)

    game.viruses.state should have size players.length
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
    cell.aiState = new NullState(cell)
    cell.position = Vector2(0, 0)
    cell.target = new Vector2(100f, 100f)

    Await.result(game.update, AwaitTime)

    cell.velocity.magnitude should be > 0f
  }

  it should "create a bigger grid if there are more players" in {
    val game1 = new Game(42, List(1))
    val game2 = new Game(42, 1 to Game.MinimumNumberOfPlayerModificator + 1 toList)

    game2.grid.width should be > game1.grid.width
    game2.grid.height should be > game1.grid.height
  }

  it should "create a grid of at least 10 players" in {
    val game1 = new Game(42, List(1))
    val game2 = new Game(42, 1 to Game.MinimumNumberOfPlayerModificator toList)

    game2.grid.width shouldBe game1.grid.width
    game2.grid.height shouldBe game1.grid.height
  }

  it should "create a state with the right info" in {
    val game = new Game(42, 1 to 10 toList)

    val state = game.state

    state.players should have size 10
    //TODO add more tests as the rest gets implemented
  }

  "update" should "return a list of ScoreModification" in {
    val game = new Game(42, List(0))

    game.resources.regulars.resources = List(new Resource(Vector2(40, 0), Regular.Mass, Regular.Score))

    val player = game.players.head
    player.cells.head.position = Vector2(40, 0)
    player.cells.head.target = Vector2(40, 0)
    player.cells.head.aiState = new NullState(player.cells.head)

    // This is to ensure no movement
    game.currentTime = 0
    game.previousTime = 0
    val (resourceModifications, _) = Await.result(game.update, AwaitTime)

    resourceModifications should contain (ScoreModification(player.id, Regular.Score))
  }

  "performAction" should "update cell's targets" in {
    val game = new Game(0, List(1, 2, 3))
    val f0 = game.performAction(1, List(Action(0, false, false, 0, Position(0f, 10f))))
    val f1 = game.performAction(2, List(Action(0, false, false, 0, Position(20f, 10f))))
    val f2 = game.performAction(3, List(Action(0, false, false, 0, Position(50f, 1f))))

    Await.result(f0, AwaitTime)
    Await.result(f1, AwaitTime)
    Await.result(f2, AwaitTime)

    val state = game.state
    val p0 = state.players.find(_.id == 1).get
    val p1 = state.players.find(_.id == 2).get
    val p2 = state.players.find(_.id == 3).get

    p0.cells.find(_.id == 0).get.target should equal(Position(0f, 10f))
    p1.cells.find(_.id == 0).get.target should equal(Position(20f, 10f))
    p2.cells.find(_.id == 0).get.target should equal(Position(50f, 1f))
  }
}
