import io.aigar.game._
import com.github.jpbetz.subspace.Vector2
import io.aigar.score.{ ScoreModification, ScoreThread }
import io.aigar.controller.response.Action
import io.aigar.game.serializable.Position
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._

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

    game.actionQueue.put(ActionQueryWithId(0, 1, List(Action(0, false, false, 0, Position(0f, 10f)))))
    game.actionQueue.put(ActionQueryWithId(0, 2, List(Action(0, false, false, 0, Position(20f, 0f)))))
    game.transferActions
    game.updateGames

    val state = game.gameState(0).get
    val p1 = state.players.find(_.id == 1).get
    val p2 = state.players.find(_.id == 2).get

    p1.cells.find(_.id == 0).get.target should equal(Position(0f, 10f))
    p2.cells.find(_.id == 0).get.target should equal(Position(20f, 0f))
  }

  "updateGames" should "put ScoreModifications from games into the ScoreThread only for the ranked game" in {
    val scoreThread = mock[ScoreThread]
    val game = new GameThread(scoreThread, List(0))
    val ranked = mock[Game]
    val notRanked = mock[Game]
    game.games = List(ranked, notRanked)
    when(ranked.id).thenReturn(Game.RankedGameId)
    when(notRanked.id).thenReturn(Game.RankedGameId + 1)
    when(ranked.update(any[Float])).thenReturn(List(ScoreModification(Game.RankedGameId, 1)))
    when(notRanked.update(any[Float])).thenReturn(List(ScoreModification(Game.RankedGameId + 1, 2)))

    game.updateGames

    verify(scoreThread).addScoreModification(ScoreModification(ranked.id, 1))
  }
}
