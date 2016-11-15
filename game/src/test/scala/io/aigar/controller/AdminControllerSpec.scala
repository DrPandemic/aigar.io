import io.aigar.controller.AdminController
import io.aigar.controller.response.SetRankedDurationCommand
import io.aigar.model.{ PlayerModel, PlayerRepository }
import io.aigar.game.GameThread
import io.aigar.score.ScoreThread

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

import org.scalatra.test.specs2._
import org.specs2.matcher._
import org.specs2.specification.BeforeAfterEach

class AdminControllerSpec extends MutableScalatraSpec
    with JsonMatchers
    with BeforeAfterEach {
  implicit val jsonFormats: Formats = DefaultFormats
  sequential

  val playerRepository = new PlayerRepository(None)
  val scoreThread = new ScoreThread(playerRepository)
  val game = new GameThread(scoreThread, List(1))
  game.updateGames // run once to initialize the game states

  addServlet(new AdminController("unicorn-revenge", game, playerRepository), "/*")

  def cleanState: Unit = {
    game.actionQueue.clear()
    playerRepository.dropSchema
    playerRepository.createSchema

    playerRepository.createPlayer(PlayerModel(Some(1), "EdgQWhJ!v&", "player1", 0))
  }

  def before: Unit = cleanState
  def after: Unit = cleanState

  def patchJson[A](uri: String, body: JValue, headers: Map[String, String] = Map())(f: => A): A =
    patch(
      uri,
      compact(render(body)).getBytes("utf-8"),
      Map("Content-Type" -> "application/json") ++ headers
    )(f)

  def postJson[A](uri: String, body: JValue, headers: Map[String, String] = Map())(f: => A): A =
    post(
      uri,
      compact(render(body)).getBytes("utf-8"),
      Map("Content-Type" -> "application/json") ++ headers
    )(f)

  val defaultActionJson = ("administrator_password" -> "unicorn-revenge")

  "POST on GameController" should {
    "403 when the administrator password doesn't match" in {
      val action = ("administrator_password" -> "nope")
      patchJson("/ranked", action) {
        status must_== 403
      }
    }

    "not 403 when the administrator password matches" in {
      patchJson("/ranked", defaultActionJson) {
        status must_!= 403
      }
    }
  }

  "PATCH /ranked" should {
    "put the action in the admin queue" in {
      game.adminCommandQueue.isEmpty() must be_==(true)
      patchJson("ranked", defaultActionJson ~ ("duration" -> 10)) {
        status must_== 200

        game.adminCommandQueue.isEmpty() must be_==(false)
        val command = game.adminCommandQueue.take()
        command must haveClass[SetRankedDurationCommand]
        command.asInstanceOf[SetRankedDurationCommand].duration must be_==(10)
      }
    }
  }

  "POST /player" should {
    "seed the players if the query contains seed" in {
      postJson("player", defaultActionJson ~ ("seed" -> true)) {
        status must_== 200

        playerRepository.getPlayers.size must_== 15
      }
    }

    "not seed the players if the query doesn't contain seed" in {
      postJson("player", defaultActionJson) {
        status must_== 200

        playerRepository.getPlayers.size must_== 1
      }
    }
  }
}
