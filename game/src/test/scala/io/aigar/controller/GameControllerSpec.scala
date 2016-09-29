import io.aigar.controller._

import org.scalatra.test.specs2._
import org.specs2.matcher.JsonMatchers
import org.specs2.matcher._

class GameControlleSpec extends MutableScalatraSpec with JsonMatchers {
  addServlet(classOf[GameController], "/*")

  def anItemWith(fields: (String, Matcher[JsonType])*): Matcher[String] = {
    fields.map {
      case(name, matcher) => /(name).andHave(matcher): Matcher[String]
    }.reduceLeft(_ and _)
  }

  "GET /:id on GameController" should {
    "return status 200" in {
      get("/1") {
        status must_== 200
        body must /("data" -> anItemWith(
                      "name" -> "that game",
                      "id" -> "1"
                    )
        )
      }
    }
  }

  "POST / on GameController" should {
    "return status 200" in {
      post("/") {
        status must_== 200
      }
    }
  }

  "POST /:id/action on GameController" should {
    "return status 200" in {
      post("/123/action") {
        status must_== 200
      }
    }
  }
}
