import io.aigar.game._
import io.aigar.score.ScoreThread
import io.aigar.controller.response.Action
import io.aigar.game.serializable.Position
import org.scalatest._

class GameThreadSpec extends FlatSpec with Matchers {
  "A GameThread" should "not have a ranked game state at first" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread, List())
    game.gameState(Game.RankedGameId) shouldBe None
  }

  it should "have a ranked game state after a game update" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread, List())
    game.updateGames
    game.gameState(Game.RankedGameId) shouldBe defined
  }

  it should "not have a game with a bad ID" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread, List())
    game.updateGames
    game.gameState(1337) shouldBe empty
  }

  it should "create a ranked game with the right ID" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread, List())
    val ranked = game.createRankedGame
    ranked.id should equal (Game.RankedGameId)
  }

  "createRankedGame" should "create its action map" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread, List())
    val ranked = game.createRankedGame
    game.actionMap should contain key ranked.id
  }

  it should "empty the actionQueue" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread, List())
    game.actionQueue.put(ActionQueryWithId(1, 1, List()))

    game.transferActions

    game.actionQueue shouldBe empty
  }

  it should "fill actionMap" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread, List())
    game.actionQueue.put(ActionQueryWithId(0, 1, List(
                                             Action(1, false, false, false, 0, Position(0f, 0f)),
                                             Action(2, false, false, false, 0, Position(10f, 10f)))))
    game.actionQueue.put(ActionQueryWithId(0, 2, List(
                                             Action(1, false, false, false, 0, Position(20f, 0f)),
                                             Action(2, false, false, false, 0, Position(30f, 0f)))))
    game.transferActions

    game.actionMap.get(0).get should contain (1 -> List(
                                                Action(1, false, false, false, 0, Position(0f, 0f)),
                                                Action(2, false, false, false, 0, Position(10f, 10f))))
    game.actionMap.get(0).get should contain (2 -> List(
                                                Action(1, false, false, false, 0, Position(20f, 0f)),
                                                Action(2, false, false, false, 0, Position(30f, 0f))))
  }
}
