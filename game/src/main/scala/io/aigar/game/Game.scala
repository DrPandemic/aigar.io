package io.aigar.game

import io.aigar.game._
import com.github.jpbetz.subspace._

/**
 * Game holds the logic for an individual game being played
 * (e.g. the ranked game or a private test game).
 */
object Game {
  final val RankedGameId = 0
  final val PlayersInRankedGame = 15
}

class Game(val id: Int, playersInGame: Int) {
  val grid = new Grid(playersInGame * Grid.WidthPerPlayer, playersInGame * Grid.HeightPerPlayer)
  val players = createPlayers(playersInGame)
  var tick = 0

  def update(deltaSeconds: Float) {
    players.foreach { _.update(deltaSeconds) }

    tick += 1
  }

  def state = {
    //TODO really implement
    serializable.GameState(
        id,
        tick,
        players.map(_.state).toList,
        serializable.Food(List(serializable.Position(5,5)), List[serializable.Position](), List[serializable.Position]()),
        grid.state,
        List[serializable.Position]()
      )
  }

  def createPlayers(playersInGame: Int) = {
    val ids = 1 to playersInGame

    ids.map { new Player(_, spawnPosition) }
  }

  def spawnPosition = {
    grid.randomPosition
  }
}
