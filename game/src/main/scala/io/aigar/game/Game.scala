package io.aigar.game

/**
 * Game holds the logic for an individual game being played
 * (e.g. the ranked game or a private test game).
 */
object Game {
  final val RankedGameId = 0
}

class Game(val id: Int) {
  def update {
    //TODO implement
  }

  def state = {
    //TODO really implement
    io.aigar.game.serializable.GameState(
        id,
        5,
        List(
          io.aigar.game.serializable.Player(12, "such", 555, List(io.aigar.game.serializable.Cell(5, 5, io.aigar.game.serializable.Position(10,10), io.aigar.game.serializable.Position(10, 10)))),
          io.aigar.game.serializable.Player(13, "wow", 555, List[io.aigar.game.serializable.Cell]())
        ),
        io.aigar.game.serializable.Food(List(io.aigar.game.serializable.Position(5,5)), List[io.aigar.game.serializable.Position](), List[io.aigar.game.serializable.Position]()),
        io.aigar.game.serializable.Dimensions(10, 10),
        List[io.aigar.game.serializable.Position]()
      )
  }
}
