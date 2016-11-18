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

  it should "not create a game" in {
    val repo = new PlayerRepository(None)
    val bootstrap = new ScalatraBootstrap

    bootstrap.appInit(Some(repo))
    // let the game update once to set the state of the ranked game
    bootstrap.game.updateGames
    val games = bootstrap.game.games
    bootstrap.destroy(null)

    games shouldBe empty
  }
}
