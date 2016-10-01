package io.aigar.controller

import io.aigar.controller.response._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class GameController extends AigarStack with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  object GameStates {
    var all = List(
      GameState(
        1,
        5,
        List(
          Player(12, "such", 555, List(Cell(5, 5, Position(10,10), Direction(10, 10)))),
          Player(13, "wow", 555, List[Cell]())
        ),
        Food(List(Position(5,5)), List[Position](), List[Position]()),
        Size(10, 10),
        List[Position]()
      )
    )
  }

  case class Failure(data: String)
  case class Success(data: String)

  get("/:id") {
    GameStateResponse(
      GameStates.all find (_.id.toString() == params("id")) match {
        case Some(b) => b
        case None => halt(404)
      }
    )
  }

  post("/") {
    GameCreationResponse(GameCreation(42, "http://somewherekindasafe.xyz"))
  }

  post("/:id/action") {
    SuccessResponse("ok")
  }
}
