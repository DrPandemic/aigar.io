package io.aigar.controller

import io.aigar.game._
import io.aigar.game.serializable._
import io.aigar.controller.response._
import org.json4s.{DefaultFormats, Formats, MappingException}
import org.scalatra.json._
import scala.util.Try

class GameController(game: GameThread)
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
    try {
      val query = parse(request.body).extract[ActionQuery]
      val actions = ActionQueryWithId(params("id").toInt, query)
      game.actionQueue.put(actions)
    } catch {
      case e: MappingException => halt(422)
      case e: java.lang.NumberFormatException => halt(400)
    }

    SuccessResponse("ok")
  }
}
