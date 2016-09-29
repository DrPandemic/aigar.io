import io.aigar.controller._
import io.aigar.controller.response._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import org.scalatra.test.specs2._
import org.specs2.matcher._

class GameControlleSpec extends MutableScalatraSpec with JsonMatchers {
  protected implicit val jsonFormats: Formats = DefaultFormats
  addServlet(classOf[GameController], "/*")

  "GET /:id on GameController" should {
    "return status 200" in {
      get("/1") {
        status must_== 200

        val state = parse(body).extract[GameStateResponse].data
        state.id must be_>=(0)
        state.tick must be_>=(0)
      }
    }
  }

  "POST / on GameController" should {
    "return status 200" in {
      post("/") {
        status must_== 200

        val result = parse(body).extract[GameCreationResponse].data
        result.id must be_>=(0)
        result.url must startWith("http://")
      }
    }
  }

  "POST /:id/action on GameController" should {
    "return status 200" in {
      post("/123/action") {
        status must_== 200

        val result = parse(body).extract[SuccessResponse]
        result.data must be_==("ok")
      }
    }
  }
}
