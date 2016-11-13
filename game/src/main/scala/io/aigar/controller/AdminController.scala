package io.aigar.controller

import io.aigar.controller.response.AdminQuery
import io.aigar.game.GameThread
import io.aigar.model.PlayerRepository
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

  post("/echo") {
    parse(request.body).extract[AdminQuery]
  }
}
