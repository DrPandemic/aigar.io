import io.aigar.controller._

import org.scalatra.test.specs2._
import slick.driver.H2Driver.api._
import com.mchange.v2.c3p0.ComboPooledDataSource

class LeaderboardControllerSpec extends MutableScalatraSpec {
  val cpds = new ComboPooledDataSource
  val db = Database.forDataSource(cpds) //Should be changed (in memory)
  addServlet(new LeaderboardController(db), "/*")

  "GET / on LeaderboardController" should {
    "return status 200" in {
      get("/") {
        status must_== 200
      }
    }
  }
}
