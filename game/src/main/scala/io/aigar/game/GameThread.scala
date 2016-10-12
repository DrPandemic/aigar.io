package io.aigar.game

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

/**
 * GameThread is the thread that runs continuously through the competition that
 * takes care of updating the individual games and processing the queued inputs
 * of the players.
 */
class GameThread extends Runnable {
  val MillisecondsPerTick = 16

  private var states: Map[Int, GameState] = Map()
  private var games: List[Game] = List(createRankedGame)

  final val actionQueue: BlockingQueue[ActionQueryWithId] = new LinkedBlockingQueue[ActionQueryWithId]()

  /**
   * Safe way to get the game state of a particular game from another thread.
   */
  def gameState(gameId: Int) = { states get gameId }

  def createRankedGame = {
    new Game(Game.RankedGameId)
  }

  def run {
    while (true) {
      updateGames

      Thread.sleep(MillisecondsPerTick)
    }
  }

  def updateGames {
    for (game <- games) {
      game.update

      states = states + (game.id -> game.state)
    }
  }
}
