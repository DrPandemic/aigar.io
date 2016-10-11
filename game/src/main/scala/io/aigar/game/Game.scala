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

class Game(val id: Int, playersCount: Int = Game.PlayersInRankedGame) {
  val grid = new Grid(playersCount * Grid.WidthPerPlayer, playersCount * Grid.HeightPerPlayer)
  val players = initPlayers(playersCount)
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

  def initPlayers(playersCount: Int) = {
    val ids = 1 to playersCount

    ids.map { new Player(_, spawnPosition) }
  }

  def spawnPosition = {
    grid.randomPosition
  }
}
