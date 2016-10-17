package io.aigar.game

import io.aigar.score.ScoreThread
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

/**
 * GameThread is the thread that runs continuously through the competition that
 * takes care of updating the individual games and processing the queued inputs
 * of the players.
 */
class GameThread(scoreThread: ScoreThread) extends Runnable {
  val MillisecondsPerTick = 16

  private var states: Map[Int, io.aigar.game.serializable.GameState] = Map()
  private var games: List[Game] = List(createRankedGame)

  final val actionQueue: BlockingQueue[ActionQueryWithId] = new LinkedBlockingQueue[ActionQueryWithId]()

  var previousTime = 0f
  var currentTime = MillisecondsPerTick / 1000f // avoid having an initial 0 delta time

  /**
   * Safe way to get the game state of a particular game from another thread.
   */
  def gameState(gameId: Int) = { states get gameId }

  def createRankedGame = {
    new Game(Game.RankedGameId, List())
  }

  def run {
    while (true) {
      updateGames

      Thread.sleep(MillisecondsPerTick)
    }
  }

  def updateGames {
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
