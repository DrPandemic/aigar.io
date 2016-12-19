import io.aigar.controller.response.GameCreationCommand
import io.aigar.controller.response.{SetRankedDurationCommand, RestartThreadCommand}
import io.aigar.game.{ActionQueryWithId, Game, GameThread, serializable}
import io.aigar.score.{ScoreModification, ScoreThread}
import io.aigar.controller.response.Action
import io.aigar.game.serializable.Position
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.{verify, when}

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
    val ranked = game.games.get(Game.RankedGameId).get

    ranked.duration shouldBe Game.DefaultDuration
  }

  it should "not be started" in {
    val scoreThread = new ScoreThread(null)
    val game = new GameThread(scoreThread)

    game.started shouldBe false
  }

  "sending RestartThreadCommand" should "put started to true" in {
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

  "sendig GameCreationCommand" should "create a new game" in {
    val gameThread = createStartedGameThread()
    gameThread.adminCommandQueue.put(GameCreationCommand(42))
    gameThread.transferAdminCommands

    val game = gameThread.games(42)

    game.id shouldBe 42
    game.players should have length Game.PrivateGameBotQuantity + 1
    game.duration shouldBe Game.PrivateGameDuration
  }

  it should "replace the game when it already exists" in {
    val gameThread = createStartedGameThread()
    gameThread.adminCommandQueue.put(GameCreationCommand(42))
    gameThread.transferAdminCommands

    val game = gameThread.games(42)

    gameThread.adminCommandQueue.put(GameCreationCommand(42))
    gameThread.transferAdminCommands

    game should not be theSameInstanceAs(gameThread.games(42))
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

  it should "set the nextRankedDuration" in {
    val game = createStartedGameThread()
    game.adminCommandQueue.put(SetRankedDurationCommand(1337))

    game.transferAdminCommands

    game.nextRankedDuration shouldBe 1337
  }

  it should "create a private game" in {
    val game = createStartedGameThread()
    game.games.toList should have length 1
    game.adminCommandQueue.put(GameCreationCommand(42))

    game.transferAdminCommands

    game.games.toList should have length 2
  }

  "updateGames" should "put ScoreModifications from games into the ScoreThread only for the ranked game" in {
    val scoreThread = mock[ScoreThread]
    val game = new GameThread(scoreThread)
    game.adminCommandQueue.put(RestartThreadCommand(List(0)))
    game.transferAdminCommands
    val ranked = mock[Game]
    val notRanked = mock[Game]
    game.games = Map(Game.RankedGameId -> ranked, Game.RankedGameId + 1 -> notRanked)
    when(ranked.id).thenReturn(Game.RankedGameId)
    when(ranked.startTime).thenReturn(Game.time)
    when(ranked.duration).thenReturn(Int.MaxValue)
    when(notRanked.id).thenReturn(Game.RankedGameId + 1)
    when(ranked.update).thenReturn((List(ScoreModification(Game.RankedGameId, 1)),
                                    serializable.GameState(0,0,0f,List(), serializable.Resources(List(), List(), List()), serializable.Dimensions(0, 0), List())))
    when(notRanked.update).thenReturn((List(ScoreModification(Game.RankedGameId + 1, 2)),
                                       serializable.GameState(0,0,0f,List(), serializable.Resources(List(), List(), List()), serializable.Dimensions(0, 0), List())))

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
    game.games = Map(Game.RankedGameId -> ranked)

    game.updateGames

    game.games.get(Game.RankedGameId) should not be None
    game.games.get(Game.RankedGameId).get should not be theSameInstanceAs(ranked)
  }

  it should "remove finished private games" in {
    val scoreThread = mock[ScoreThread]
    val ranked = mock[Game]
    when(ranked.id).thenReturn(Game.RankedGameId)
    when(ranked.startTime).thenReturn(0L)
    when(ranked.duration).thenReturn(10)
    val privateGame = mock[Game]
    when(privateGame.id).thenReturn(Game.RankedGameId + 1)
    when(privateGame.startTime).thenReturn(0L)
    when(privateGame.duration).thenReturn(0)
    val game = new GameThread(scoreThread)
    game.adminCommandQueue.put(RestartThreadCommand(List(0)))
    game.transferAdminCommands
    game.games = Map(Game.RankedGameId -> ranked, privateGame.id -> privateGame)

    game.updateGames

    game.games.get(Game.RankedGameId + 1) shouldBe None
  }

  it should "remove finished private game's state" in {
    val game = createStartedGameThread()
    val id = Game.RankedGameId + 1
    val privateGame = mock[Game]
    when(privateGame.id).thenReturn(id)
    when(privateGame.timeLeft).thenReturn(50)
    when(privateGame.update).thenReturn((List(),
                                         serializable.GameState(0,0,0f,List(), serializable.Resources(List(), List(), List()), serializable.Dimensions(0, 0), List())))
    game.games += (id -> privateGame)

    game.updateGames
    game.gameState(id) should not be None

    when(privateGame.timeLeft).thenReturn(0)
    game.updateGames
    game.gameState(id) shouldBe None
  }
}
