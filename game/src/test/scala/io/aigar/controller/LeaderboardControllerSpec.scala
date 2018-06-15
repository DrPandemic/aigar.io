import io.aigar.controller._
import io.aigar.controller.response._
import io.aigar.model._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import org.scalatra.test.specs2._
import org.specs2.matcher._
import org.specs2.specification.BeforeAfterEach

class LeaderboardControllerSpec extends MutableScalatraSpec
    with BeforeAfterEach
    with io.aigar.test.TestWithDatabase {
  implicit val jsonFormats: Formats = DefaultFormats
  sequential

  addServlet(new LeaderboardController(playerRepository), "/*")

  def cleanState = {
    cleanDB()

    playerRepository.createPlayer(PlayerModel(Some(1), "EdgQWhJ!v&", "player1"))
    playerRepository.createPlayer(PlayerModel(Some(2), "EdgQWhJ!v&2", "player2"))
    playerRepository.createPlayer(PlayerModel(Some(3), "EdgQWhJ!v&3", "player3"))

    scoreRepository.addScore(1, 5f)
    scoreRepository.addScore(2, 10f)
    scoreRepository.addScore(3, 15f)
  }

  def before = cleanState
  def after = cleanState

  "GET / on LeaderboardController" should {
    "return the right data format" in {
      get("/") {
        status must_== 200

        val entries = parse(body).extract[LeaderboardResponse].data
        entries.foreach(entry => {
                          entry.player_id must be_>=(0)
                          entry.score must be_>=(0f)
                        })

        val playerIds = entries.map(_.player_id)
        playerIds.distinct.size must be_==(playerIds.size)
      }
    }

    "return many entries for each player" in {
      scoreRepository.addScore(1, 15f)
      scoreRepository.addScore(2, 42f)

      get("/") {
        status must_== 200

        val entries = parse(body).extract[LeaderboardResponse].data.map {
          case LeaderboardEntry(id, name, score, _) => (id, name, score)
        }

        entries should contain(allOf(
          (1, "player1", 5f),
          (1, "player1", 15f),
          (2, "player2", 10f),
          (2, "player2", 42f),
          (3, "player3", 15f)
        ))
      }
    }
  }
}
