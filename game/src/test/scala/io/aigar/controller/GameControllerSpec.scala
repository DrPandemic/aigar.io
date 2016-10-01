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
    "return the right data format" in {
      get("/1") {
        status must_== 200

        parse(body).extract[GameStateResponse] must not(throwAn[MappingException])
      }
    }
  }

  "POST / on GameController" should {
    "return the right data format" in {
      post("/") {
        status must_== 200

        val result = parse(body).extract[GameCreationResponse].data
        result.url must startWith("http://")
      }
    }
  }

  "POST /:id/action on GameController" should {
    "return the right data format" in {
      post("/123/action") {
        status must_== 200

        val result = parse(body).extract[SuccessResponse]
        result.data must be_==("ok")
      }
    }
  }
}
