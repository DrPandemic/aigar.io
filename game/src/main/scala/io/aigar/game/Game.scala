package io.aigar.game

import com.typesafe.scalalogging.LazyLogging
import io.aigar.score.ScoreModification
import io.aigar.controller.response.Action

/**
 * Game holds the logic for an individual game being played
 * (e.g. the ranked game or a private test game).
 */
object Game {
  final val RankedGameId = 0
  final val DefaultDuration = 60 * 20
}

class Game(val id: Int, playerIDs: List[Int], val duration: Int = Game.DefaultDuration) extends LazyLogging {
  logger.info(s"Launching game with ID $id.")
  val grid = new Grid(playerIDs.length * Grid.WidthPerPlayer, playerIDs.length * Grid.HeightPerPlayer)
  val players = createPlayers
  val viruses = new Viruses(grid)
  val resources = new Resources(grid)
  val startTime = GameThread.time
  var tick = 0

  def update(deltaSeconds: Float): List[ScoreModification] = {
    var modifications = List[ScoreModification]()

    players.foreach { player => modifications :::= player.update(deltaSeconds, grid, players) }
    modifications :::= viruses.update(grid, players)
    modifications :::= resources.update(grid, players)
    tick += 1

    modifications
  }

  def performAction(player_id: Int, actions: List[Action]): List[ScoreModification] = {
    var modifications = List[ScoreModification]()
    players.find(_.id == player_id) match {
      case Some(player) => {
        modifications = player.performAction(actions)
      }
      case None =>
    }
    modifications
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
