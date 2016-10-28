package io.aigar.game

import io.aigar.controller.response.Action
import scala.math.round
import com.github.jpbetz.subspace._

class Player(val id: Int, startPosition: Vector2) {
  var cells = List(new Cell(0, startPosition))

  def update(deltaSeconds: Float, grid: Grid, players: List[Player]) {
    val opponents = players.filterNot(_ == this)
    cells.foreach { cell => cell.update(deltaSeconds, grid) }
    cells.foreach { _.eats(opponents)}
  }

  def state = {
    val mass = round(cells.map(_.mass).sum).toInt
    serializable.Player(id,
                        id.toString,
                        mass,
                        isActive,
                        cells.map(_.state).toList)
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
   * command coming from the AI of a team).
   */
  def onExternalAction = {
    cells.foreach { _.behavior.onPlayerActivity }
  }

  /**
    * The player is active when there is an active cell.
    */
  def isActive():Boolean = {
    cells.exists(_.behavior.isActive)
  }

  /**
    * Removes dead cell from the player's cell list
    */
  def removeCell(cell: Cell): Unit ={
    cells = cells.filterNot(_ == cell)
  }
}
