import io.aigar.game._
import org.scalatest._

class GameThreadSpec extends FlatSpec with Matchers {
  "A GameThread" should "not have a ranked game state at first" in {
    val game = new GameThread
    game.gameState(Game.RankedGameId) shouldBe None
  }

  it should "have a ranked game state after a game update" in {
    val game = new GameThread
    game.updateGames
    game.gameState(Game.RankedGameId) shouldBe defined
  }

  it should "not have a game with a bad ID" in {
    val game = new GameThread
    game.updateGames
    game.gameState(1337) shouldBe empty
  }

  it should "create a ranked game with the right ID" in {
    val game = new GameThread
    val ranked = game.createRankedGame
    ranked.id should equal (Game.RankedGameId)
  }
}
