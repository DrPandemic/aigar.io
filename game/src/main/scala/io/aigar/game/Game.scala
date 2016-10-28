package io.aigar.game

import io.aigar.controller.response.Action
import scala.collection.immutable.HashMap

/**
 * Game holds the logic for an individual game being played
 * (e.g. the ranked game or a private test game).
 */
object Game {
  final val RankedGameId = 0
}

class Game(val id: Int, playerIDs: List[Int]) {
  val grid = new Grid(playerIDs.length * Grid.WidthPerPlayer, playerIDs.length * Grid.HeightPerPlayer)
  val players = createPlayers
  val resources = new Resources(grid)
  var tick = 0

  def update(deltaSeconds: Float): Unit = {
    players.foreach { player => player.update(deltaSeconds, grid, players) }
    resources.update(players)

    tick += 1
  }

  def performAction(player_id: Int, actions: List[Action]): Unit = {
    players.find(_.id == player_id) match {
      case Some(player) => player.performAction(actions)
      case None => {}
    }
  }

  def state = {
    //TODO really implement and update spec to add tests
    serializable.GameState(
        id,
        tick,
        players.map(_.state).toList,
        resources.state,
        grid.state,
        List[serializable.Position]()
      )
  }

  def createPlayers = {
    playerIDs.map { new Player(_, spawnPosition) }
  }

  def spawnPosition = {
    grid.randomPosition
  }
}
