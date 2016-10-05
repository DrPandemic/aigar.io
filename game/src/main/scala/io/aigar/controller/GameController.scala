package io.aigar.controller

import io.aigar.game._
import io.aigar.controller.response._
import org.json4s.{DefaultFormats, Formats, MappingException}
import org.scalatra.json._
import scala.util.Try

class GameController(val game: GameThread)
  extends AigarStack with JacksonJsonSupport {

  get("/:id") {
    GameStateResponse(
      Try(params("id").toInt).toOption match {
        case Some(id) => game.gameState(id) match {
          case Some(state) => state
          case None => halt(404)
        }
        case None => halt(400)
      }
    )
  }

  post("/") {
    GameCreationResponse(GameCreation(42, "http://somewherekindasafe.xyz"))
  }

  post("/:id/action") {
    val actions = try {
      parse(request.body).extract[ActionQuery].actions
    } catch {
      case e: MappingException => halt(422)
    }

    SuccessResponse("ok")
  }
}
