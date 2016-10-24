package io.aigar.game

/**
 * Game holds the logic for an individual game being played
 * (e.g. the ranked game or a private test game).
 */
object Game {
  final val RankedGameId = 0
}

class Game(val id: Int, playerIDs: List[Int]) {
  val grid = new Grid(playerIDs.length * Grid.WidthPerPlayer, playerIDs.length * Grid.HeightPerPlayer)
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
    playerIDs.map { new Player(_, spawnPosition) }
  }

  def spawnPosition = {
    grid.randomPosition
  }
}
