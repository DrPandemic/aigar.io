package io.aigar.controller

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import slick.driver.H2Driver.api._

class GameController (db: Database) extends AigarStack with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  case class Game(id: String, name: String)
  object GameData {
    var all = List(
      Game("0", "360 no scope"),
      Game("1", "that game")
    )
  }

  abstract class Response
  case class Failure(message: String) extends Response
  case class GameSuccesses(data: List[Game]) extends Response
  case class GameSuccess(data: Game) extends Response
  case class Success(data: String) extends Response

  get("/") {
    GameSuccesses(
      GameData.all
    )
  }

  get("/:id") {
    GameSuccess(
      GameData.all find (_.id == params("id")) match {
        case Some(b) => b
        case None => halt(404)
      }
    )
  }

  post("/") {
    Success("Well done, the game was created")
  }

  post("/:id/action") {
    Success("Are you sure you want to do that?")
  }
}
