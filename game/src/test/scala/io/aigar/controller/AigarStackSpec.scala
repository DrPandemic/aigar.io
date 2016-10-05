import io.aigar.controller._
import io.aigar.controller.response.{ErrorResponse}

import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.JsonMethods._

import org.scalatra.test.specs2._
import org.specs2.matcher._

object ConcreteStack extends AigarStack {
  get("/:status") {
    halt(params("status").toInt)
  }
}

class AigarStackSpec extends MutableScalatraSpec {
  implicit val jsonFormats: Formats = DefaultFormats

  addServlet(ConcreteStack, "/*")

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

    "400 should return a generic error" in {
      get("/400") {
        status must_== 400
        val result = parse(body).extract[ErrorResponse].error
        result must_==("invalid request")
      }
    }
  }
}
