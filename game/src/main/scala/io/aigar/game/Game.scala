package io.aigar.game

/**
 * Game holds the logic for an individual game being played
 * (e.g. the ranked game or a private test game).
 */
object Game {
  final val RankedGameId = 0
  final val PlayersInRankedGame = 15
}

class Game(val id: Int, playersInGame: Int) {
  val grid = new Grid(playersInGame * Grid.WidthPerPlayer, playersInGame * Grid.HeightPerPlayer)
  val players = createPlayers
  val resources = new Resources(grid)
  var tick = 0

  def update(deltaSeconds: Float) {
    players.foreach { _.update(deltaSeconds, grid) }
    resources.update
    
    tick += 1
  }

  def state = {
    //TODO really implement and update spec to add tests
    serializable.GameState(
        id,
        tick,
        players.map(_.state).toList,
        resources.state,
        grid.state,
        List[serializable.Position]()
      )
  }

  def createPlayers = {
    val ids = 1 to playersInGame

    ids.map { new Player(_, spawnPosition) }
  }

  def spawnPosition = {
    grid.randomPosition
  }
}
