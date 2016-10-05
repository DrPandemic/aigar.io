package io.aigar.controller

import io.aigar.game._
import io.aigar.controller.response._
import org.json4s.{DefaultFormats, Formats, MappingException}
import org.scalatra.json._

class GameController extends AigarStack with JacksonJsonSupport {
  get("/:id") {
    GameStateResponse(
      // TODO get from GameThread
      halt(404)
      // GameStates.all find (_.id.toString() == params("id")) match {
      //   case Some(b) => b
      //   case None => halt(404)
      // }
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
