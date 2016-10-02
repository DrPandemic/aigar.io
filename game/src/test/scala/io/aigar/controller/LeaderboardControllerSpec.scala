import io.aigar.controller._

import org.scalatra.test.specs2._
import io.aigar.HelperSpec

class LeaderboardControllerSpec extends MutableScalatraSpec with HelperSpec{
  addServlet(new LeaderboardController(getDatabase()), "/*")

  "GET / on LeaderboardController" should {
    "return status 200" in {
      get("/") {
        status must_== 200
      }
    }
  }
}
