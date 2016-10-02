package io.aigar.controller

import io.aigar.controller.response._
import org.json4s.{DefaultFormats, Formats, MappingException}
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
    val actions = try {
      parse(request.body).extract[ActionQuery].actions
    } catch {
      case e: MappingException => halt(422)
    }

    SuccessResponse("ok")
  }
}
