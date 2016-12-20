package io.aigar.game

import com.typesafe.scalalogging.LazyLogging
import io.aigar.controller.response.{
  AdminCommand,
  GameCreationCommand,
  SetRankedDurationCommand,
  RestartThreadCommand
}
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.Future
import scala.collection.JavaConversions._
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
    var actions = new java.util.ArrayList[ActionQueryWithId]()
    actionQueue.drainTo(actions)

    val futures = actions.toList.map(action =>
      games.get(action.game_id) match {
        case Some(game) => (game, game.performAction(action.player_id, action.actions))
      }
    )

    futures.foreach {
      case(game, future) => {
        Try(Await.result(future, Game.MillisecondsPerTick milliseconds)) match {
          case Success(result) => applyScoreModifications(game, result)
          case Failure(error) => logger.error(s"Game with id $game.id failed to perform actions with $error.getGessage")
        }
      }
    }
  }

  def transferAdminCommands: Unit = {
    while (!adminCommandQueue.isEmpty) {
      adminCommandQueue.take match {
        case command: SetRankedDurationCommand => nextRankedDuration = command.duration
        case command: RestartThreadCommand => restart(command.playerIDs)
        case command: GameCreationCommand => games += (command.gameId -> createPrivateGame(command.gameId))
      }
    }
  }

  private def resetGames: Unit = {
    // Remove games
    games = games.filter {
      case (Game.RankedGameId, _) => true
      case (_, game) => game.timeLeft > 0f
    }

    // Remove states
    states = states.filter {
      case (id, _) if games.contains(id) => true
      case _ => false
    }

    // Reset ranked
    games.get(Game.RankedGameId) match {
      case Some(ranked) => {
        val elapsed = Game.time - ranked.startTime
        if (ranked.duration < elapsed) {
          games += (Game.RankedGameId -> createRankedGame)
        }
      }
      case None =>
    }
  }

  def updateGames: Unit = {
    resetGames

    val futures = games.values.map(game => (game, game.update))

    futures.foreach {
      case (game, future) => {
        Try(Await.result(future, Game.MillisecondsPerTick milliseconds)) match {
          case Success((modifications, state)) => {
            applyScoreModifications(game, modifications)
            states = states + (game.id -> state)
          }
          case Failure(error) => logger.error(s"Game with id $game.id failed to update with $error.getGessage")
        }
      }
    }
  }

  def applyScoreModifications(game: Game, modifications: List[ScoreModification]): Unit = {
    if (game.id == Game.RankedGameId) {
      modifications.foreach { scoreThread.addScoreModification(_) }
    }
  }
}
