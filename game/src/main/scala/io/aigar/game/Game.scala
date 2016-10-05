package io.aigar.game

/**
 * Game holds the logic for an individual game being played
 * (e.g. the ranked game or a private test game).
 */
object Game {
  val RankedGameId = 0
}

class Game(val id: Int) {
  def update {
    //TODO implement
  }

  def state = {
    //TODO really implement
    GameState(
        1,
        5,
        List(
          Player(12, "such", 555, List(Cell(5, 5, Position(10,10), Position(10, 10)))),
          Player(13, "wow", 555, List[Cell]())
        ),
        Food(List(Position(5,5)), List[Position](), List[Position]()),
        Dimensions(10, 10),
        List[Position]()
      )
  }
}
