import io.aigar.controller._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import org.scalatra.test.specs2._
import org.specs2.matcher._

object ConcreteStack extends AigarStack {
  get("/:id") {
    halt(params("id").toInt)
  }
}

class AigarStackSpec extends MutableScalatraSpec {
  "errors" should {
    "404 should return a generic error" in {
      get("/404") {
        status must_== 404
        val result = parse(body).extract[ErrorResponse].error
        result must_==("not found")
      }
    }

    "422 should return a generic error" in {
      get("/422") {
        status must_== 422
        val result = parse(body).extract[ErrorResponse].error
        result must_==("unprocessable entity")
      }
    }
  }
}
