import io.aigar.controller.AdminController
import io.aigar.controller.response._
import io.aigar.model._
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
    with BeforeAfterEach
    with io.aigar.test.TestWithDatabase {
  implicit val jsonFormats: Formats = DefaultFormats
  sequential

  val scoreThread = new ScoreThread(playerRepository)
  val game = new GameThread(scoreThread)
  game.adminCommandQueue.put(RestartThreadCommand(List(1)))
  game.transferAdminCommands
  game.updateGames // run once to initialize the game states

  addServlet(new AdminController("unicorn-revenge", game, playerRepository, scoreRepository), "/*")

  def cleanState: Unit = {
    game.actionQueue.clear()
    cleanDB()

    playerRepository.createPlayer(PlayerModel(Some(1), "EdgQWhJ!v&", "player1", 0))
    playerRepository.createPlayer(PlayerModel(Some(2), "SUPERSECRET", "player2", 0))
  }

  def before: Unit = cleanState
  def after: Unit = cleanState

  def putJson[A](uri: String, body: JValue, headers: Map[String, String] = Map())(f: => A): A =
    put(
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
      putJson("/ranked", action) {
        status must_== 403
      }
    }

    "not 403 when the administrator password matches" in {
      putJson("/ranked", defaultActionJson) {
        status must_!= 403
      }
    }
  }

  "PUT /ranked" should {
    "put the action in the admin queue" in {
      game.adminCommandQueue.isEmpty() must be_==(true)
      putJson("ranked", defaultActionJson ~ ("duration" -> 10)) {
        status must_== 200

        game.adminCommandQueue.isEmpty() must be_==(false)
        val command = game.adminCommandQueue.take()
        command must haveClass[SetRankedDurationCommand]
        command.asInstanceOf[SetRankedDurationCommand].duration must be_==(10)
      }
    }
  }

  "POST /player" should {
    "can seed a custom number of player" in {
      postJson("player", defaultActionJson ~ ("seed" -> true) ~ ("playerCount" -> 12)) {
        status must_== 200

        playerRepository.getPlayers.size must_== 12
      }
    }

    "not seed the players if the query doesn't contain seed" in {
      postJson("player", defaultActionJson) {
        playerRepository.getPlayers.size must_== 2
      }
    }

    "be able to create new player" in {
      postJson("player", defaultActionJson ~ ("player_name" -> "foo")) {
        status must_== 200

        playerRepository.getPlayers.size must_== 3
        playerRepository.getPlayers must contain((player:PlayerModel) => player.playerName == "foo")
      }
    }

    "on success, return the new player information" in {
      postJson("player", defaultActionJson ~ ("player_name" -> "foo")) {
        val player = playerRepository.getPlayers.find(_.playerName == "foo").get
        val secret = player.playerSecret
        val id = player.id.get
        status must_== 200

        parse(body).extract[CreatePlayerResponse] must not(throwAn[MappingException])

        val parsedResponse = parse(body).extract[CreatePlayerResponse]
        parsedResponse.data.player_secret must_== secret
        parsedResponse.data.player_id must_== id
      }
    }
  }

  "PUT /competition" should {
    "put the action in the admin queue" in {
      game.adminCommandQueue.isEmpty() must beTrue
      putJson("competition", defaultActionJson ~ ("running" -> true)) {
        status must_== 200

        game.adminCommandQueue.isEmpty() must beFalse
        val command = game.adminCommandQueue.take()
        command must haveClass[RestartThreadCommand]
        command.asInstanceOf[RestartThreadCommand].playerIDs must be_==(List(1, 2))
      }
    }
  }

  "PUT /ranked" should {
    "put the action in the admin queue" in {
      game.adminCommandQueue.isEmpty() must be_==(true)
      putJson("ranked", defaultActionJson ~ ("duration" -> 10)) {
        status must_== 200

        game.adminCommandQueue.isEmpty() must be_==(false)
        val command = game.adminCommandQueue.take()
        command must haveClass[SetRankedDurationCommand]
        command.asInstanceOf[SetRankedDurationCommand].duration must be_==(10)
      }
    }
  }

  "PUT /multiplier" should {
    "put the action in the admin queue" in {
      game.adminCommandQueue.isEmpty() must be_==(true)
      putJson("multiplier", defaultActionJson ~ ("multiplier" -> 10)) {
        status must_== 200

        game.adminCommandQueue.isEmpty() must be_==(false)
        val command = game.adminCommandQueue.take()
        command must haveClass[SetRankedMultiplierCommand]
        command.asInstanceOf[SetRankedMultiplierCommand].multiplier must be_==(10)
      }
    }
  }

  "PUT /paused" should {
    "put the action in the admin queue" in {
      game.adminCommandQueue.isEmpty() must be_==(true)
      putJson("paused", defaultActionJson ~ ("paused" -> true)) {
        status must_== 200

        game.adminCommandQueue.isEmpty() must be_==(false)
        val command = game.adminCommandQueue.take()
        command must haveClass[PauseCommand]
        command.asInstanceOf[PauseCommand].paused must be_==(true)
      }
    }
  }

  "POST /get_players on AdminController" should {
    "return the right data format" in {
      postJson("get_players", defaultActionJson) {
        status must_== 200

        val entries = parse(body).extract[AdminPlayerResponse].data
        entries.foreach(entry => { entry.player_id must be_>=(0) })

        val player_ids = entries.map(_.player_id)
        player_ids.distinct.size must be_==(player_ids.size)
      }
    }

    "return an entry for each player" in {
      postJson("get_players", defaultActionJson) {
        status must_== 200

        val entries = parse(body).extract[AdminPlayerResponse].data
        val players = entries.map(entry => { (entry.player_id, entry.secret) })
        val db_players = playerRepository.getPlayers.map(entry => { (entry.id.get, entry.playerSecret) })

        players must be_==(db_players)
      }
    }
  }
}
