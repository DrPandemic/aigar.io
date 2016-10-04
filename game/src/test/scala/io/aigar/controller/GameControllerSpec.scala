import io.aigar.controller._

import org.scalatra.test.specs2._

class GameControllerSpec extends MutableScalatraSpec {
  addServlet(classOf[GameController], "/*")

  "GET / on GameController" should {
    "return status 200" in {
      get("/") {
        status must_== 200
      }
    }
  }

  "GET /:id on GameController" should {
    "return status 200" in {
      get("/1") {
        status must_== 200
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
