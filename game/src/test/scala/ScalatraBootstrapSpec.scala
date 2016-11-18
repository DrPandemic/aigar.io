import org.scalatest._

import io.aigar.model._
import io.aigar.game._

class ScalatraBootstrapSpec extends FlatSpec with Matchers {
  "ScalatraBootstrap" should "use a fixed player repository on init when passed as a parameter" in {
    val repo = new PlayerRepository(None)
    val bootstrap = new ScalatraBootstrap

    bootstrap.appInit(Some(repo))
    val players = bootstrap.playerRepository
    bootstrap.destroy(null)

    players should be theSameInstanceAs(repo)
  }

  it should "create a game with the players from the player repository on init" in {
    val repo = new PlayerRepository(None)
    val player1 = repo.createPlayer(PlayerModel(None, "secret",  "player1", 20))
    val player2 = repo.createPlayer(PlayerModel(None, "secret?", "player2", 10))
    val player3 = repo.createPlayer(PlayerModel(None, "secret!", "player3", 30))
    val bootstrap = new ScalatraBootstrap

    bootstrap.appInit(Some(repo))
    val expectedIds = repo.getPlayers.map(_.id).flatten

    // let the game update once to set the state of the ranked game
    bootstrap.game.transferAdminCommands
    bootstrap.game.updateGames
    val state = bootstrap.game.gameState(Game.RankedGameId)
    bootstrap.destroy(null)

    state should not be(None)
    state.get.players.map(_.id) should contain theSameElementsAs(expectedIds)
  }
}
