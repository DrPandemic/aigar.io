package io.aigar.game

import com.typesafe.scalalogging.LazyLogging
import scala.math.round
import io.aigar.controller.response.{ AdminCommand, SetRankedDurationCommand }
import io.aigar.score.ScoreThread
import java.util.concurrent.LinkedBlockingQueue

/**
 * GameThread is the thread that runs continuously through the competition that
 * takes care of updating the individual games and processing the queued inputs
 * of the players.
 */
object GameThread {
  final val NanoSecondsPerMillisecond = 1000000f
  final val MillisecondsPerSecond = 1000f
  final val NanoSecondsPerSecond = NanoSecondsPerMillisecond * MillisecondsPerSecond

  final val TicksPerSecond = 15
  final val MillisecondsPerTick = round(MillisecondsPerSecond / TicksPerSecond)

  /**
    * Current time, in seconds.
    */
  def time: Float = {
    System.nanoTime / NanoSecondsPerSecond
  }
}

class GameThread(scoreThread: ScoreThread, playerIDs: List[Int]) extends Runnable
                                                                 with LazyLogging {
  logger.info("Starting Game thread.")

  final val actionQueue = new LinkedBlockingQueue[ActionQueryWithId]()
  final val adminCommandQueue = new LinkedBlockingQueue[AdminCommand]()

  var nextRankedDuration = Game.DefaultDuration
  private var states: Map[Int, serializable.GameState] = Map()
  var games: List[Game] = List(createRankedGame)

  var running = true
  var previousTime = 0f
  var currentTime = GameThread.MillisecondsPerTick / GameThread.MillisecondsPerSecond // avoid having an initial 0 delta time

  /**
   * Safe way to get the game state of a particular game from another thread.
   */
  def gameState(gameId: Int): Option[serializable.GameState] = {
    states get gameId
  }

  def createRankedGame: Game = {
    new Game(Game.RankedGameId, playerIDs, nextRankedDuration)
  }

  def run: Unit = {
    while (running) {
      transferActions
      transferAdminCommands
      updateGames

      Thread.sleep(GameThread.MillisecondsPerTick)
    }
  }

  def transferActions: Unit = {
    while (!actionQueue.isEmpty) {
      val action = actionQueue.take
      games.find(_.id == action.game_id) match {
        case Some(game) => {
          val modifications = game.performAction(action.player_id, action.actions)
          if (game.id == Game.RankedGameId) {
            modifications.foreach {
              scoreThread.addScoreModification(_)
            }
          }
        }
        case None =>
      }
    }
  }

  def transferAdminCommands: Unit = {
    while(!adminCommandQueue.isEmpty) {
      adminCommandQueue.take match {
        case command: SetRankedDurationCommand => nextRankedDuration = command.duration
      }
    }
  }

  private def resetRankedGameIfExpired: Unit = {
    games.find(_.id == Game.RankedGameId) match {
      case Some(ranked) => {
        val elapsed = GameThread.time - ranked.startTime
        if(ranked.duration < elapsed) {
          games = games diff List(ranked)
          games = createRankedGame :: games
        }
      }
      case None =>
    }
  }

  def updateGames: Unit = {
    resetRankedGameIfExpired

    for (game <- games) {
      val deltaTime = currentTime - previousTime
      val modifications = game.update(deltaTime)
      if(game.id == Game.RankedGameId) {
        modifications.foreach { scoreThread.addScoreModification(_) }
      }

      game.update(deltaTime)
      states = states + (game.id -> game.state)

      previousTime = currentTime
      currentTime = GameThread.time
    }
  }
}
