package io.aigar.controller

import io.aigar.controller.response.{
  AdminQuery,
  SetRankedDurationCommand,
  RestartThreadCommand,
  SetRankedDurationQuery,
  RestartThreadQuery,
  SeedPlayersQuery,
  CreatePlayerQuery,
  CreatePlayerResponse,
  PlayerResult,
  SuccessResponse
}
import io.aigar.model.PlayerModel
import scala.util.Success
import scala.util.Try
import io.aigar.game.GameThread
import io.aigar.model.{
  PlayerRepository,
  seed
}
import org.json4s.MappingException
import org.scalatra.MethodOverride
import org.scalatra.json.JacksonJsonSupport

class AdminController(password: String, game: GameThread, playerRepository: PlayerRepository)
    extends AigarStack
    with JacksonJsonSupport
    with MethodOverride {

  before() {
    try {
      if(parse(request.body).extract[AdminQuery].administrator_password != password) {
        halt(403)
      }
    } catch {
      case e: MappingException => halt(422)
    }
  }

  private def createRandomSecret(): String = {
    (new scala.util.Random(new java.security.SecureRandom())).alphanumeric.take(16).mkString
  }

  post("/player") {
    val result = parse(request.body)
    Try(result.extract[SeedPlayersQuery]).orElse(Try(result.extract[CreatePlayerQuery])) match {
      case Success(query: SeedPlayersQuery) => {
        if (query.seed) seed.seedPlayers(playerRepository)
        SuccessResponse("ok")
      }
      case Success(query: CreatePlayerQuery) => {
        val player = playerRepository.createPlayer(PlayerModel(None, createRandomSecret, query.player_name, 0))
        CreatePlayerResponse(PlayerResult(player.playerSecret, player.id.get))
      }
      case _ => halt(422)
    }
  }

  put("/ranked") {
    try {
      val query = parse(request.body).extract[SetRankedDurationQuery]
      val command = SetRankedDurationCommand(query.duration)
      game.adminCommandQueue.put(command)
    } catch {
      case e: MappingException => halt(422)
    }

    SuccessResponse("ok")
  }

  def fetchPlayerIDs: List[Int] = {
    val players = playerRepository.getPlayers()

    players.map(_.id).flatten  // only keep IDs that are not None
  }

  put("/competition") {
    try {
      val query = parse(request.body).extract[RestartThreadQuery]
      if (query.running) {
        val command = RestartThreadCommand(fetchPlayerIDs)
        game.adminCommandQueue.put(command)
      }
    } catch {
      case e: MappingException => halt(422)
    }

    SuccessResponse("ok")
  }
}
