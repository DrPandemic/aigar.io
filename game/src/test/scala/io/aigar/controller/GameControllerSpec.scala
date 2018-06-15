import io.aigar.game._
import io.aigar.score.ScoreThread
import io.aigar.controller._
import io.aigar.controller.response._
import io.aigar.model._

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

import org.scalatra.test.specs2._
import org.specs2.matcher._
import org.specs2.specification.BeforeAfterEach

class GameControllerSpec extends MutableScalatraSpec
    with JsonMatchers
    with BeforeAfterEach
    with io.aigar.test.TestWithDatabase {
  implicit val jsonFormats: Formats = DefaultFormats
  sequential

  val scoreThread = new ScoreThread(scoreRepository)
  val game = new GameThread(scoreThread)
  game.adminCommandQueue.put(RestartThreadCommand(List(1)))
  game.transferAdminCommands
  game.updateGames // run once to initialize the game states

  addServlet(new GameController(game, playerRepository), "/*")

  def cleanState = {
    game.actionQueue.clear()
    game.adminCommandQueue.clear()
    cleanDB()

    playerRepository.createPlayer(PlayerModel(Some(1), "EdgQWhJ!v&", "player1"))
  }

  def before = cleanState
  def after = cleanState

  def postJson[A](uri: String, body: JValue, headers: Map[String, String] = Map())(f: => A): A =
    post(
      uri,
      compact(render(body)).getBytes("utf-8"),
      Map("Content-Type" -> "application/json") ++ headers
    )(f)

  val defaultActionJson =
    ("player_secret" -> "EdgQWhJ!v&") ~
    ("actions" ->
      List(
        ("cell_id" -> 123) ~
        ("burst" -> false) ~
        ("split" -> true) ~
        ("trade" -> 0) ~
        ("target" -> ("x" -> 10) ~ ("y" -> 10)),

        ("cell_id" -> 1234) ~
        ("burst" -> false) ~
        ("split" -> true) ~
        ("trade" -> 0) ~
        ("target" -> ("x" -> 10) ~ ("y" -> 10))
      )
    )

  "GET /{the ranked game} on GameController" should {
    "return a parsable GameStateResponse" in {
      get("/" + Game.RankedGameId) {
        status must_== 200

        parse(body).extract[GameStateResponse] must not(throwAn[MappingException])

        val parsedResponse = parse(body).extract[GameStateResponse]
        parsedResponse.data.players(0).name must_== "player1"
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
    "returns a success" in {
      postJson("/", ("player_secret" -> "EdgQWhJ!v&")) {
        status must_== 200

        val response = parse(body).extract[GameCreationResponse]
        // Which is the player's id
        response.data.id must_== 1
      }
    }

    "puts the action in the admin queue" in {
      game.adminCommandQueue.isEmpty() must be_==(true)
      postJson("/", ("player_secret" -> "EdgQWhJ!v&")) {
        status must_== 200

        game.adminCommandQueue.isEmpty() must be_==(false)
        val action = game.adminCommandQueue.take().asInstanceOf[GameCreationCommand]
        action.gameId must_== 1
      }
    }

    "403 when player's secret doesn't match" in {
      postJson("/", ("player_secret" -> "nope")) {
        status must_== 403
      }
    }
  }

  "POST /:id/action on GameController" should {
    "returns a success" in {
      postJson("/1/action", defaultActionJson) {
        status must_== 200

        val result = parse(body).extract[SuccessResponse]
        result.data must be_==("ok")
      }
    }

    "puts the action in the game queue" in {
      game.actionQueue.isEmpty() must be_==(true)
      postJson("/1/action", defaultActionJson) {
        status must_== 200

        game.actionQueue.isEmpty() must be_==(false)
        val action = game.actionQueue.take()
        action.game_id must be_==(1)
        action.actions.length must be_==(2)
        action.actions(0).cell_id must be_==(123)
      }
    }

    "fails with bad arguments" in {
      postJson("/1/action", ("something" -> "42")) {
        status must_== 422
      }
    }

    "fails with bad game's id" in {
      postJson("/nope/action", defaultActionJson) {
        status must_== 400
      }
    }

    "403 when the player secret doesn't match" in {
      val action =
        ("player_secret" -> "nope") ~
          ("actions" ->
             List(
               ("cell_id" -> 123) ~
                 ("burst" -> false) ~
                 ("split" -> true) ~
                 ("trade" -> 0) ~
                 ("target" -> ("x" -> 10) ~ ("y" -> 10))
             )
          )
      postJson("/1/action", action) {
        status must_== 403
      }
    }
  }
}
