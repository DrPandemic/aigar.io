import org.scalatest._

import io.aigar.model._
import io.aigar.game._

class ScalatraBootstrapSpec extends FlatSpec with Matchers {
  "ScalatraBootstrap" should "create repositories" in {
    val db = AigarDatabase.createDatabase(AigarDatabase.getRandomName, true)
    val bootstrap = new ScalatraBootstrap

    bootstrap.appInit(db, true)
    val players = bootstrap.playerRepository
    val scores = bootstrap.scoreRepository
    bootstrap.destroy(null)

    players shouldNot be(null)
    scores shouldNot be(null)
  }

  it should "not create a game" in {
    val db = AigarDatabase.createDatabase(AigarDatabase.getRandomName, true)
    val bootstrap = new ScalatraBootstrap

    bootstrap.appInit(db, true)
    // let the game update once to set the state of the ranked game
    bootstrap.game.updateGames
    val games = bootstrap.game.games
    bootstrap.destroy(null)

    games shouldBe empty
  }
}
