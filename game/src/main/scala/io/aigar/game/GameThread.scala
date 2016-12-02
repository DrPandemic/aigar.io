package io.aigar.game

import com.typesafe.scalalogging.LazyLogging
import io.aigar.controller.response.{
  AdminCommand,
  GameCreationCommand,
  SetRankedDurationCommand,
  RestartThreadCommand
}
import io.aigar.score.{ScoreModification, ScoreThread}
import java.util.concurrent.LinkedBlockingQueue

/**
 * GameThread is the thread that runs continuously through the competition that
 * takes care of updating the individual games and processing the queued inputs
 * of the players.
 */
class GameThread(scoreThread: ScoreThread) extends Runnable
                                           with LazyLogging {
  logger.info("Starting Game thread.")

  final val actionQueue = new LinkedBlockingQueue[ActionQueryWithId]()
  final val adminCommandQueue = new LinkedBlockingQueue[AdminCommand]()

  var playerIDs: List[Int] = List()

  var nextRankedDuration = Game.DefaultDuration
  private var states: Map[Int, serializable.GameState] = Map()
  var games: Map[Int, Game] = Map()

  var running = true
  var started = false

  def restart(playerIDs: List[Int]): Unit = {
    actionQueue.clear
    adminCommandQueue.clear
    this.playerIDs = playerIDs
    games = Map(Game.RankedGameId -> createRankedGame)

    started = true
  }

  /**
   * Safe way to get the game state of a particular game from another thread.
   */
  def gameState(gameId: Int): Option[serializable.GameState] = {
    states get gameId
  }

  def createRankedGame: Game = {
    new Game(Game.RankedGameId, playerIDs, nextRankedDuration)
  }

  def createPrivateGame(gameId: Int): Game = {
    // We add the game's id since it's also the player's id at the same time
    val playerIds = gameId :: ((-1 * Game.PrivateGameBotQuantity) to -1).toList
    new Game(gameId, playerIds, Game.PrivateGameDuration)
  }

  def run: Unit = {
    while (running) {
      transferAdminCommands
      if (started) {
        transferActions
        updateGames
      }

      Thread.sleep(Game.MillisecondsPerTick)
    }
  }

  def transferActions: Unit = {
    while (!actionQueue.isEmpty) {
      val action = actionQueue.take
      games.get(action.game_id) match {
        case Some(game) => {
          val modifications = game.performAction(action.player_id, action.actions)
          applyScoreModifications(game, modifications)
        }
        case None =>
      }
    }
  }

  def transferAdminCommands: Unit = {
    while (!adminCommandQueue.isEmpty) {
      adminCommandQueue.take match {
        case command: SetRankedDurationCommand => nextRankedDuration = command.duration
        case command: RestartThreadCommand => restart(command.playerIDs)
        case command: GameCreationCommand => games = games + (command.gameId -> createPrivateGame(command.gameId))
      }
    }
  }

  private def resetRankedGameIfExpired: Unit = {
    games.get(Game.RankedGameId) match {
      case Some(ranked) => {
        val elapsed = Game.time - ranked.startTime
        if (ranked.duration < elapsed) {
          games = games - Game.RankedGameId
          games = games + (Game.RankedGameId -> createRankedGame)
        }
      }
      case None =>
    }
  }

  def updateGames: Unit = {
    resetRankedGameIfExpired

    for (game <- games.values) {
      val modifications = game.update
      applyScoreModifications(game, modifications)
      states = states + (game.id -> game.state)
    }
  }

  def applyScoreModifications(game: Game, modifications: List[ScoreModification]): Unit = {
    if (game.id == Game.RankedGameId) {
      modifications.foreach { scoreThread.addScoreModification(_) }
    }
  }
}
