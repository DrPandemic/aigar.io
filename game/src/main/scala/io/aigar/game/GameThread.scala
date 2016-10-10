package io.aigar.game

/**
 * GameThread is the thread that runs continuously through the competition that
 * takes care of updating the individual games and processing the queued inputs
 * of the players.
 */
class GameThread extends Runnable {
  val MillisecondsPerTick = 16

  private var states: Map[Int, io.aigar.game.serializable.GameState] = Map()
  private var games: List[Game] = List(createRankedGame)

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
