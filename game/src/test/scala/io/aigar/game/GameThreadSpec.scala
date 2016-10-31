import io.aigar.game._
import io.aigar.score.ScoreThread
import io.aigar.controller.response.Action
import io.aigar.game.serializable.Position
import org.scalatest._

class GameThreadSpec extends FlatSpec with Matchers {
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

  "transferActions" should "empty the actionQueue" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread, List())
    game.actionQueue.put(ActionQueryWithId(1, 1, List()))

    game.transferActions

    game.actionQueue shouldBe empty
  }

  it should "update cell's targets" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread, List(1, 2))

    game.actionQueue.put(ActionQueryWithId(0, 1, List(Action(0, false, false, false, 0, Position(0f, 10f)))))
    game.actionQueue.put(ActionQueryWithId(0, 2, List(Action(0, false, false, false, 0, Position(20f, 0f)))))
    game.transferActions
    game.updateGames

    val state = game.gameState(0).get
    val p1 = state.players.find(_.id == 1).get
    val p2 = state.players.find(_.id == 2).get

    p1.cells.find(_.id == 0).get.target should equal(Position(0f, 10f))
    p2.cells.find(_.id == 0).get.target should equal(Position(20f, 0f))
  }
}
