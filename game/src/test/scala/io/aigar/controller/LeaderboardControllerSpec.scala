import io.aigar.controller._
import io.aigar.controller.response._
import io.aigar.model.TeamRepository

import org.json4s._
import org.json4s.jackson.JsonMethods._

import org.scalatra.test.specs2._
import org.specs2.matcher._

class LeaderboardControllerSpec extends MutableScalatraSpec {
  protected implicit val jsonFormats: Formats = DefaultFormats
  val teamRepository = new TeamRepository(None)
  addServlet(new LeaderboardController(teamRepository), "/*")

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
  }
}
