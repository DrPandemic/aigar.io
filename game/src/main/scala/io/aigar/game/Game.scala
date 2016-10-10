package io.aigar.game

import io.aigar.game._

/**
 * Game holds the logic for an individual game being played
 * (e.g. the ranked game or a private test game).
 */
object Game {
  final val RankedGameId = 0
}

class Game(val id: Int) {
  var tick = 0

  def update {
    //TODO implement
    tick += 1
  }

  def state = {
    //TODO really implement
    serializable.GameState(
        id,
        tick,
        List(
          serializable.Player(12, "such", 555, List(serializable.Cell(5, 5, serializable.Position(10,10), serializable.Position(10, 10)))),
          serializable.Player(13, "wow", 555, List[serializable.Cell]())
        ),
        serializable.Food(List(serializable.Position(5,5)), List[serializable.Position](), List[serializable.Position]()),
        serializable.Dimensions(10, 10),
        List[serializable.Position]()
      )
  }
}
