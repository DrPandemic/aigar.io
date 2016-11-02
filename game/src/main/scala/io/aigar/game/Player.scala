package io.aigar.game

import io.aigar.controller.response.Action
import scala.math.round
import com.github.jpbetz.subspace.Vector2

class Player(val id: Int, startPosition: Vector2) {
  private var currentCellId: Int = 0
  var cells = List(new Cell(currentCellId, startPosition))

  def update(deltaSeconds: Float, grid: Grid, players: List[Player]): Unit = {
    if ( cells.isEmpty ) {
      currentCellId += 1
      cells = List(new Cell(currentCellId, grid.randomPosition))
    }
    val opponents = players.filterNot(_ == this)
    cells.foreach { _.update(deltaSeconds, grid) }
    cells.foreach { _.eats(opponents) }
  }

  def state: serializable.Player = {
    val mass = round(cells.map(_.mass).sum).toInt
    serializable.Player(id,
                        id.toString,
                        mass,
                        isActive,
                        cells.map(_.state)
    )
  }

  def performAction(actions: List[Action]): Unit = {
    onExternalAction

    actions.foreach {
      action => cells.find(_.id == action.cell_id) match {
        case Some(cell) => cell.performAction(action)
        case None => {}
      }
    }
  }

  /**
   * Should be called whenever an external action occurs (e.g. we receive a
   * command coming from the AI of a player).
   */
  def onExternalAction = {
    cells.foreach { _.aiState.onPlayerActivity }
  }

  /**
    * The player is active when there is an active cell.
    */
  def isActive():Boolean = {
    cells.exists(_.aiState.isActive)
  }

  /**
    * Removes dead cell from the player's cell list
    */
  def removeCell(cell: Cell): Unit ={
    cells = cells.filterNot(_ == cell)
  }
}
