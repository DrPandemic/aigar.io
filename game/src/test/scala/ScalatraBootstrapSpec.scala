import org.scalatest._

import io.aigar.model._
import io.aigar.game._

class ScalatraBootstrapSpec extends FlatSpec with Matchers {
  def withInMemDatabase(testCode: (TeamRepository) => Any) {
    val teamRepository = new TeamRepository(None)
    val team1 = teamRepository.createTeam(Team(None, "secret",  "team1", 20))
    val team2 = teamRepository.createTeam(Team(None, "secret?", "team2", 10))
    val team3 = teamRepository.createTeam(Team(None, "secret!", "team3", 30))

    try{
      testCode(teamRepository)
    }
    finally {
      teamRepository.dropSchema
      teamRepository.closeConnection
    }
  }

  "Creating the application" should "create a game with the players from the team repository" in withInMemDatabase { (teamRepository) =>
    val bootstrap = new ScalatraBootstrap(teamRepository)
    val expectedIds = teamRepository.getTeams.map(_.id).flatten

    // let the game update once to set the state of the ranked game
    bootstrap.game.updateGames
    val state = bootstrap.game.gameState(Game.RankedGameId)

    state should not be(None)
    state.get.players.map(_.id) should contain theSameElementsAs(expectedIds)
  }
}
