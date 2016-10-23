import io.aigar.game._
import io.aigar.score.ScoreThread
import org.scalatest._

class GameThreadSpec extends FlatSpec with Matchers {
  "A GameThread" should "not have a ranked game state at first" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread)
    game.gameState(Game.RankedGameId) shouldBe None
  }

  it should "have a ranked game state after a game update" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread)
    game.updateGames
    game.gameState(Game.RankedGameId) shouldBe defined
  }

  it should "not have a game with a bad ID" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread)
    game.updateGames
    game.gameState(1337) shouldBe empty
  }

  it should "create a ranked game with the right ID" in {
    val scoreThread = new ScoreThread
    val game = new GameThread(scoreThread)
    val ranked = game.createRankedGame
    ranked.id should equal (Game.RankedGameId)
  }
}
