import io.aigar.controller._
import io.aigar.controller.response._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import org.scalatra.test.specs2._
import org.specs2.matcher._

class LeaderboardControllerSpec extends MutableScalatraSpec {
  protected implicit val jsonFormats: Formats = DefaultFormats
  addServlet(classOf[LeaderboardController], "/*")

  "GET / on LeaderboardController" should {
    "return the right data format" in {
      get("/") {
        status must_== 200

        val entries = parse(body).extract[LeaderboardResponse].data
        entries.foreach(entry => {
                          entry.team_id must be_>=(0)
                          entry.score must be_>=(0)
                        })

        val longerSize = entries.groupBy(_.team_id).foldLeft(0)(_ max _._2.size)
        longerSize must_== 1
      }
    }
  }
}
