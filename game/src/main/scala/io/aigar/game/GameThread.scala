package io.aigar.game

/**
 * GameThread is the thread that runs continuously through the competition that
 * takes care of updating the individual games and processing the queued inputs
 * of the players.
 */
class GameThread extends Runnable {
  val MillisecondsPerTick = 16

  private var _states: Map[Int, GameState] = Map()
  private var _games: List[Game] = List(createRankedGame)

  /**
   * Safe way to get the game state of a particular game from another thread.
   */
  def gameState(gameId: Int) = { _states get gameId }

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
    for (game <- _games) {
      game.update

      _states = _states + (game.id -> game.state)
    }
  }
}
