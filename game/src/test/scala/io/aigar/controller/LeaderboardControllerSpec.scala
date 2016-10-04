import io.aigar.controller._

import org.scalatra.test.specs2._

class LeaderboardControllerSpec extends MutableScalatraSpec {
  addServlet(classOf[LeaderboardController], "/*")

  "GET / on LeaderboardController" should {
    "return status 200" in {
      get("/") {
        status must_== 200
      }
    }
  }
}
