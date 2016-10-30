import io.aigar.game._
import com.github.jpbetz.subspace.Vector2
import io.aigar.score.{ ScoreModification, ScoreThread }
import io.aigar.controller.response.Action
import io.aigar.game.serializable.Position
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class GameThreadSpec extends FlatSpec with Matchers with MockitoSugar {
  "A GameThread" should "not have a ranked game state at first" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread, List())
    game.gameState(Game.RankedGameId) shouldBe None
  }

  it should "have a ranked game state after a game update" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread, List())
    game.updateGames
    game.gameState(Game.RankedGameId) shouldBe defined
  }

  it should "not have a game with a bad ID" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread, List())
    game.updateGames
    game.gameState(1337) shouldBe empty
  }

  it should "create a ranked game with the right ID" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread, List())
    val ranked = game.createRankedGame
    ranked.id should equal (Game.RankedGameId)
  }

  "createRankedGame" should "create its action map" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread, List())
    val ranked = game.createRankedGame
    game.gameActions should contain key ranked.id
  }

  "transferActions" should "empty the actionQueue" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread, List())
    game.actionQueue.put(ActionQueryWithId(1, 1, List()))

    game.transferActions

    game.actionQueue shouldBe empty
  }

  it should "fill gameActions" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread, List())
    game.actionQueue.put(ActionQueryWithId(0, 1, List(
                                             Action(1, false, false, false, 0, Position(0f, 0f)),
                                             Action(2, false, false, false, 0, Position(10f, 10f)))))
    game.actionQueue.put(ActionQueryWithId(0, 2, List(
                                             Action(1, false, false, false, 0, Position(20f, 0f)),
                                             Action(2, false, false, false, 0, Position(30f, 0f)))))
    game.transferActions

    game.gameActions.get(0).get should contain (1 -> List(
                                                Action(1, false, false, false, 0, Position(0f, 0f)),
                                                Action(2, false, false, false, 0, Position(10f, 10f))))
    game.gameActions.get(0).get should contain (2 -> List(
                                                Action(1, false, false, false, 0, Position(20f, 0f)),
                                                Action(2, false, false, false, 0, Position(30f, 0f))))
  }

  "updateGames" should "put ScoreModifications from games into the ScoreThread" in {
    val scoreThread = mock[ScoreThread]
    val game = new GameThread(scoreThread, List(0))
    val ranked = game.createRankedGame
    ranked.resources.regular.positions = List(Vector2(40, 0))
    val player = ranked.players.head
    player.cells.head.position = Vector2(40, 0)
    player.cells.head.target = Vector2(40, 0)
    player.cells.foreach { cell => cell.behavior = new NoBehavior(cell) }

    game.updateGames

    verify(scoreThread, atLeastOnce()).addScoreModification(ScoreModification(0, Regular.Score))
  }
}
