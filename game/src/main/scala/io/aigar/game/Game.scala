package io.aigar.game

import io.aigar.score.ScoreModification
import io.aigar.controller.response.Action
import scala.collection.mutable.MutableList

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
  val viruses = new Viruses(grid)
  val resources = new Resources(grid)
  var tick = 0

  def update(deltaSeconds: Float): MutableList[ScoreModification] = {
    players.foreach { player => player.update(deltaSeconds, grid, players) }
    viruses.update(grid, players)

    val scoreModifications = resources.update(grid, players)

    tick += 1

    scoreModifications
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
        players.map(_.state),
        resources.state,
        grid.state,
        viruses.state
      )
  }

  def createPlayers = {
    playerIDs.map { new Player(_, spawnPosition) }
  }

  def spawnPosition = {
    grid.randomPosition
  }
}
