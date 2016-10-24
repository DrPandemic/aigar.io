import org.scalatest._

import io.aigar.model._
import io.aigar.game._

class ScalatraBootstrapSpec extends FlatSpec with Matchers {
  "ScalatraBootstrap" should "use a fixed team repository on init when passed as a parameter" in {
    val repo = new TeamRepository(None)
    val bootstrap = new ScalatraBootstrap

    bootstrap.appInit(Some(repo))
    val teams = bootstrap.teamRepository
    bootstrap.destroy(null)

    teams should be theSameInstanceAs(repo)
  }

  it should "create a game with the players from the team repository on init" in {
    val repo = new TeamRepository(None)
    val team1 = repo.createTeam(Team(None, "secret",  "team1", 20))
    val team2 = repo.createTeam(Team(None, "secret?", "team2", 10))
    val team3 = repo.createTeam(Team(None, "secret!", "team3", 30))
    val bootstrap = new ScalatraBootstrap

    bootstrap.appInit(Some(repo))
    val expectedIds = repo.getTeams.map(_.id).flatten

    // let the game update once to set the state of the ranked game
    bootstrap.game.updateGames
    val state = bootstrap.game.gameState(Game.RankedGameId)
    bootstrap.destroy(null)

    state should not be(None)
    state.get.players.map(_.id) should contain theSameElementsAs(expectedIds)
  }
}
