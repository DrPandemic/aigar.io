package io.aigar.controller

import io.aigar.controller.response.{
  AdminQuery,
  SetRankedDurationCommand,
  SetRankedDurationQuery,
  SeedPlayersQuery,
  SuccessResponse
}
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

  post("/player") {
    try {
      val query = parse(request.body).extract[SeedPlayersQuery]

      if(query.seed) {
        seed.seedPlayers(playerRepository)
      }
    } catch {
      case e: MappingException => // Do the normal query
    }

    SuccessResponse("ok")
  }

  put("/ranked") {
    try {
      val query = parse(request.body).extract[SetRankedDurationQuery]
      val command = SetRankedDurationCommand(query.duration)
      game.adminCommandQueue.put(command)
    } catch {
      case e: MappingException => halt(422)
      case e: java.lang.NumberFormatException => halt(400)
    }

    SuccessResponse("ok")
  }
}
