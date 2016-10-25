package io.aigar.game

import io.aigar.score.ScoreThread
import io.aigar.controller.response.Action
import java.util.concurrent.LinkedBlockingQueue
import scala.collection.mutable.HashMap

/**
 * GameThread is the thread that runs continuously through the competition that
 * takes care of updating the individual games and processing the queued inputs
 * of the players.
 */
class GameThread(scoreThread: ScoreThread, teamIDs: List[Int]) extends Runnable {
  val MillisecondsPerTick = 16

  private var states: Map[Int, serializable.GameState] = Map()
  private var games: List[Game] = List(createRankedGame)

  final val actionQueue = new LinkedBlockingQueue[ActionQueryWithId]()
  final val actionMap = new HashMap[Int, HashMap[Int, List[Action]]]()

  var running = true

  var previousTime = 0f
  var currentTime = MillisecondsPerTick / 1000f // avoid having an initial 0 delta time

  /**
   * Safe way to get the game state of a particular game from another thread.
   */
  def gameState(gameId: Int): Option[serializable.GameState] = {
    states get gameId
  }

  def createRankedGame: Game = {
   // actionMap(Game.RankedGameId) = new HashMap()
    new Game(Game.RankedGameId, teamIDs)
  }

  def run: Unit = {
    while (running) {
      updateGames

      Thread.sleep(MillisecondsPerTick)
    }
  }

  def transferActions: Unit = {

  }

  def updateGames: Unit = {
    for (game <- games) {
      val deltaTime = currentTime - previousTime
      game.update(deltaTime)

      states = states + (game.id -> game.state)

      previousTime = currentTime
      currentTime = time
    }
  }

  /**
   * Current time, in seconds.
   */
  final val NanoSecondsPerMillisecond = 1000000f
  final val MillisecondsPerSecond = 1000f
  final val NanoSecondsPerSecond = NanoSecondsPerMillisecond * MillisecondsPerSecond
  def time: Float = {
    System.nanoTime / NanoSecondsPerSecond
  }
}
