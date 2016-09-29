package io.aigar.controller

import io.aigar.controller.response._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class GameController extends AigarStack with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  object GameData {
    var all = List(
      GameState(1, 13),
      GameState(1, 50)
    )
  }

  case class Failure(data: String)
  case class Success(data: String)

  get("/:id") {
    GameData.all find (_.id.toString() == params("id")) match {
      case Some(b) => b
      case None => halt(404)
    }
  }

  post("/") {
    Success("Well done, the game was created")
  }

  post("/:id/action") {
    Success("Are you sure you want to do that?")
  }
}
