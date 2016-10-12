import io.aigar.game._
import io.aigar.controller._
import io.aigar.controller.response._

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

import org.scalatra.test.specs2._
import org.specs2.matcher._
import org.specs2.specification.BeforeAfterEach

class GameControllerSpec extends MutableScalatraSpec
    with JsonMatchers
    with BeforeAfterEach {
  implicit val jsonFormats: Formats = DefaultFormats
  sequential

  val game = new GameThread
  game.updateGames // run once to initialize the game states
  addServlet(new GameController(game), "/*")

  def cleanGame = {
    game.actionQueue.clear()
  }

  def before = cleanGame
  def after = cleanGame

  def postJson[A](uri: String, body: JValue, headers: Map[String, String] = Map())(f: => A): A =
    post(
      uri,
      compact(render(body)).getBytes("utf-8"),
      Map("Content-Type" -> "application/json") ++ headers
    )(f)

  val defaultActionJson =
    ("team_secret" -> "so secret") ~
    ("actions" ->
      List(
        ("cell_id" -> 123) ~
        ("burst" -> false) ~
        ("split" -> true) ~
        ("feed" -> false) ~
        ("trade" -> 0) ~
        ("target" -> ("x" -> 10) ~ ("y" -> 10)),

        ("cell_id" -> 1234) ~
        ("burst" -> false) ~
        ("split" -> true) ~
        ("feed" -> false) ~
        ("trade" -> 0) ~
        ("target" -> ("x" -> 10) ~ ("y" -> 10))
      )
    )

  "GET /{the ranked game} on GameController" should {
    "return a parsable GameStateResponse" in {
      get("/" + Game.RankedGameId) {
        status must_== 200

        parse(body).extract[GameStateResponse] must not(throwAn[MappingException])
      }
    }
  }

  "GET /hello on GameController" should {
    "fail with a bad request (invalid ID)" in {
      get("/hello") {
        status must_== 400
      }
    }
  }

  "GET /1337 (invalid ID) on GameController" should {
    "fail with a not found error" in {
      get("/1337") {
        status must_== 404
      }
    }
  }

  "POST / on GameController" should {
    "return an URL to watch the game" in {
      post("/") {
        status must_== 200

        val result = parse(body).extract[GameCreationResponse].data
        result.url must startWith("http://")
      }
    }
  }

  "POST /:id/action on GameController" should {
    "return a success" in {
      postJson("/1/action", defaultActionJson) {
        status must_== 200

        val result = parse(body).extract[SuccessResponse]
        result.data must be_==("ok")
      }
    }

    "fails with bad arguments" in {
      postJson("1/action", ("something" -> "42")) {
        status must_== 422
      }
    }

    "fails with bad game's id" in {
      postJson("nope/action", defaultActionJson) {
        status must_== 400
      }
    }
  }
}
