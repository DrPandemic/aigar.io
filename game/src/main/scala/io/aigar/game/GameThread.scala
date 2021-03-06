package io.aigar.game

import scala.math.round
import com.typesafe.scalalogging.LazyLogging
import io.aigar.controller.response.{
  AdminCommand,
  GameCreationCommand,
  SetRankedDurationCommand,
  RestartThreadCommand,
  SetRankedMultiplierCommand,
  PauseCommand,
  DisableLeaderboardCommand
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

  var previousTime = Game.time
  var currentTime = previousTime + Game.MillisecondsPerTick / Game.MillisecondsPerSecond // avoid having an initial 0 delta time

  final val actionQueue = new LinkedBlockingQueue[ActionQueryWithId]()
  final val adminCommandQueue = new LinkedBlockingQueue[AdminCommand]()

  var playerIDs: List[Int] = List()

  var nextRankedDuration = Game.DefaultDuration
  var nextRankedMultiplier = Game.DefaultMutliplier
  private var states: Map[Int, serializable.GameState] = Map()
  var games: Map[Int, Game] = Map()

  var paused = false

  var running = true
  var started = false
  var disabledLeaderboard = false

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
    states get gameId match {
      case Some(state) => {
        state.paused = paused
        state.disabledLeaderboard = disabledLeaderboard
        Some(state)
      }
      case None => None
    }
  }

  def createRankedGame: Game = {
    new Game(Game.RankedGameId, playerIDs, nextRankedDuration, nextRankedMultiplier)
  }

  def createPrivateGame(gameId: Int): Game = {
    // We add the game's id since it's also the player's id at the same time
    val playerIds = gameId :: ((-1 * Game.PrivateGameBotQuantity) to -1).toList
    new Game(gameId, playerIds, Game.PrivateGameDuration)
  }

  def run: Unit = {
    while (running) {
      transferAdminCommands
      if (started && !paused) {
        transferActions
        updateGames
      }

      if (paused) {
        actionQueue.clear
        games.foreach { case (_, g) => g.updatePaused }
      }

      Thread.sleep(math.max(0, round(Game.MillisecondsPerTick - (currentTime - previousTime) * Game.MillisecondsPerSecond)))
      previousTime = currentTime
      currentTime = Game.time
    }
  }

  def transferActions: Unit = {
    val actions = new java.util.ArrayList[ActionQueryWithId]()
    actionQueue.drainTo(actions)

    val futures = actions
      .map(action =>
      games.get(action.game_id) match {
        case Some(game) => (game, game.performAction(action.player_id, action.actions))
        case None => null
      }
      ).filter({
        case (_, _) => true
        case _ => false
      })

    futures.foreach {
      case(game, future) => {
        Try(Await.result(future, Game.MillisecondsPerTick milliseconds)) match {
          case Success(result) => applyScoreModifications(game, result)
          case Failure(error) => logger.error(s"Game with id ${game.id} failed to perform actions with ${error.printStackTrace()}")
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
        case command: SetRankedMultiplierCommand => nextRankedMultiplier = command.multiplier
        case command: PauseCommand => paused = command.paused
        case command: DisableLeaderboardCommand => disabledLeaderboard = command.disabled
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
        if (ranked.timeLeft <= 0f) {
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
      modifications.foreach { scoreThread.addScoreModification(_, game.multiplier) }
    }
  }
}
