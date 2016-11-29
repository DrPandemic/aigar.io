import io.aigar.game._
import io.aigar.controller.response.{SetRankedDurationCommand, RestartThreadCommand}
import io.aigar.score.{ScoreModification, ScoreThread}
import io.aigar.controller.response.Action
import io.aigar.game.serializable.Position
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._

class GameThreadSpec extends FlatSpec with Matchers with MockitoSugar {
  def createStartedGameThread(playerIDs: List[Int] = List()): GameThread = {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread)

    game.adminCommandQueue.put(RestartThreadCommand(playerIDs))
    game.transferAdminCommands

    game
  }

  "A GameThread" should "not have a ranked game state at first" in {
    val game = createStartedGameThread()
    game.gameState(Game.RankedGameId) shouldBe None
  }

  it should "have a ranked game state after a game update" in {
    val game = createStartedGameThread()
    game.updateGames
    game.gameState(Game.RankedGameId) shouldBe defined
  }

  it should "not have a game with a bad ID" in {
    val game = createStartedGameThread()
    game.updateGames
    game.gameState(1337) shouldBe empty
  }

  it should "create a ranked game with the right ID" in {
    val game = createStartedGameThread()
    val ranked = game.createRankedGame
    ranked.id should equal (Game.RankedGameId)
  }

  it should "create a ranked game with the initial duration" in {
    val game = createStartedGameThread()
    val ranked = game.games.find(_.id == Game.RankedGameId).get

    ranked.duration shouldBe Game.DefaultDuration
  }

  it should "not be started" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread)

    game.started shouldBe false
  }

  "send RestartThreadCommand" should "put started to true" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread)
    game.adminCommandQueue.put(RestartThreadCommand(List()))
    game.transferAdminCommands

    game shouldBe 'started
  }

  it should "reset state" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread)
    game.actionQueue.put(ActionQueryWithId(1, 1, List()))
    game.adminCommandQueue.put(SetRankedDurationCommand(10))
    game.playerIDs = List(42)
    val oldGames = game.games

    game.adminCommandQueue.put(RestartThreadCommand(List()))
    game.transferAdminCommands

    game.actionQueue shouldBe empty
    game.adminCommandQueue shouldBe empty
    game.playerIDs shouldBe empty
    game.games should not be theSameInstanceAs(oldGames)
  }

  it should "keep the nextRankedDuration" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread)
    game.nextRankedDuration = 42

    game.adminCommandQueue.put(RestartThreadCommand(List()))
    game.transferAdminCommands

    game.nextRankedDuration shouldBe 42
  }

  it should "apply the new player's ids" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread)
    game.playerIDs = List(0)

    game.adminCommandQueue.put(RestartThreadCommand(List(42)))
    game.transferAdminCommands

    game.playerIDs shouldBe List(42)
  }

  "createRankedGame" should "use the duration from the game thread" in {
    val game = createStartedGameThread()
    game.nextRankedDuration = 1337
    val ranked = game.createRankedGame
    ranked.duration should equal (1337)
  }

  "transferActions" should "empty the actionQueue" in {
    val game = createStartedGameThread()
    game.actionQueue.put(ActionQueryWithId(1, 1, List()))

    game.transferActions

    game.actionQueue shouldBe empty
  }

  it should "update cell's targets" in {
    val game = createStartedGameThread(List(1, 2))

    game.actionQueue.put(ActionQueryWithId(Game.RankedGameId, 1, List(Action(0, false, false, 0, Position(0f, 10f)))))
    game.actionQueue.put(ActionQueryWithId(Game.RankedGameId, 2, List(Action(0, false, false, 0, Position(20f, 0f)))))
    game.transferActions
    game.updateGames

    val state = game.gameState(Game.RankedGameId).get
    val p1 = state.players.find(_.id == 1).get
    val p2 = state.players.find(_.id == 2).get

    p1.cells.find(_.id == 0).get.target should equal(Position(0f, 10f))
    p2.cells.find(_.id == 0).get.target should equal(Position(20f, 0f))
  }

  "transferAdminCommands" should "empty the adminCommandQueue" in {
    val game = createStartedGameThread()
    game.adminCommandQueue.put(SetRankedDurationCommand(10))

    game.transferAdminCommands

    game.adminCommandQueue shouldBe empty
  }

  it should "sets the nextRankedDuration" in {
    val game = createStartedGameThread()
    game.adminCommandQueue.put(SetRankedDurationCommand(1337))

    game.transferAdminCommands

    game.nextRankedDuration shouldBe 1337
  }

  "updateGames" should "put ScoreModifications from games into the ScoreThread only for the ranked game" in {
    val scoreThread = mock[ScoreThread]
    val game = new GameThread(scoreThread)
    game.adminCommandQueue.put(RestartThreadCommand(List(0)))
    game.transferAdminCommands
    val ranked = mock[Game]
    val notRanked = mock[Game]
    game.games = List(ranked, notRanked)
    when(ranked.id).thenReturn(Game.RankedGameId)
    when(ranked.startTime).thenReturn(GameThread.time)
    when(ranked.duration).thenReturn(Int.MaxValue)
    when(notRanked.id).thenReturn(Game.RankedGameId + 1)
    when(ranked.update(any[Float])).thenReturn(List(ScoreModification(Game.RankedGameId, 1)))
    when(notRanked.update(any[Float])).thenReturn(List(ScoreModification(Game.RankedGameId + 1, 2)))

    game.updateGames

    verify(scoreThread).addScoreModification(ScoreModification(ranked.id, 1))
  }

  it should "remove the ranked game and create a new one after a given time" in {
    val scoreThread = mock[ScoreThread]
    val ranked = mock[Game]
    when(ranked.id).thenReturn(Game.RankedGameId)
    when(ranked.startTime).thenReturn(0L)
    when(ranked.duration).thenReturn(0)
    val game = new GameThread(scoreThread)
    game.adminCommandQueue.put(RestartThreadCommand(List(0)))
    game.transferAdminCommands
    game.games = List(ranked)

    game.updateGames

    game.games.find(_.id == Game.RankedGameId) should not be None
    game.games.find(_.id == Game.RankedGameId).get should not be theSameInstanceAs(ranked)
  }
}
