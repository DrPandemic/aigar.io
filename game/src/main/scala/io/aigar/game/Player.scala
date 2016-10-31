package io.aigar.game

import scala.math.round
import com.github.jpbetz.subspace._

class Player(val id: Int, startPosition: Vector2) {
  var cells = List(new Cell(0, startPosition))

  def update(deltaSeconds: Float, grid: Grid, players: List[Player]) {
    if ( cells.isEmpty ) {
      cells = List(new Cell(0, grid.randomPosition))
    }
    else {
      val opponents = players.filterNot(_ == this)
      cells.foreach { _.update(deltaSeconds, grid) }
      cells.foreach { _.eats(opponents) }
    }
  }

  def state = {
    val mass = round(cells.map(_.mass).sum).toInt
    serializable.Player(id,
                        id.toString,
                        mass,
                        isActive,
                        cells.map(_.state)
    )
  }

  /**
   * Should be called whenever an external action occurs (e.g. we receive a
   * command coming from the AI of a player).
   */
  def onExternalAction = {
    cells.foreach { _.machineState.onPlayerActivity }
  }

  /**
    * The player is active when there is an active cell.
    */
  def isActive():Boolean = {
    cells.exists(_.machineState.isActive)
  }

  /**
    * Removes dead cell from the player's cell list
    */
  def removeCell(cell: Cell): Unit ={
    cells = cells.filterNot(_ == cell)
  }
}
