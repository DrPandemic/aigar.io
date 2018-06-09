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

    playerRepository.createPlayer(PlayerModel(Some(1), "EdgQWhJ!v&", "player1", 789))
    playerRepository.createPlayer(PlayerModel(Some(2), "EdgQWhJ!v&2", "player2", 50))
    playerRepository.createPlayer(PlayerModel(Some(3), "EdgQWhJ!v&3", "player3", 5))
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

        val player_ids = entries.map(_.player_id)
        player_ids.distinct.size must be_==(player_ids.size)
      }
    }

    "return an entry for each player" in {
      get("/") {
        status must_== 200

        val entries = parse(body).extract[LeaderboardResponse].data
        val player_ids = entries.map(_.player_id)
        val db_player_ids = playerRepository.getPlayers.map(_.id.get)

        player_ids must be_==(db_player_ids)
      }
    }
  }
}
