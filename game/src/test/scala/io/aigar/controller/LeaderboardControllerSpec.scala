import io.aigar.controller._
import io.aigar.controller.response._
import io.aigar.model._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import org.scalatra.test.specs2._
import org.specs2.matcher._
import org.specs2.specification.BeforeAfterEach

class LeaderboardControllerSpec extends MutableScalatraSpec
    with BeforeAfterEach {
  implicit val jsonFormats: Formats = DefaultFormats
  sequential

  val teamRepository = new TeamRepository(None)
  addServlet(new LeaderboardController(teamRepository), "/*")

  def cleanState = {
    teamRepository.dropSchema
    teamRepository.createSchema

    teamRepository.createTeam(Team(Some(1), "EdgQWhJ!v&", "team1", 789))
    teamRepository.createTeam(Team(Some(2), "EdgQWhJ!v&2", "team2", 50))
    teamRepository.createTeam(Team(Some(3), "EdgQWhJ!v&3", "team3", 5))
  }

  def before = cleanState
  def after = cleanState

  "GET / on LeaderboardController" should {
    "return the right data format" in {
      get("/") {
        status must_== 200

        val entries = parse(body).extract[LeaderboardResponse].data
        entries.foreach(entry => {
                          entry.team_id must be_>=(0)
                          entry.score must be_>=(0)
                        })

        val team_ids = entries.map(_.team_id)
        team_ids.distinct.size must be_==(team_ids.size)
      }
    }

    "return an entry for each player" in {
      get("/") {
        status must_== 200

        val entries = parse(body).extract[LeaderboardResponse].data
        val team_ids = entries.map(_.team_id)
        val db_team_ids = teamRepository.getTeams.map(_.id.get)

        team_ids must be_==(db_team_ids)
      }
    }
  }
}
