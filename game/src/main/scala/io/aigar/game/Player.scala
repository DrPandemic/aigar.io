package io.aigar.game

import io.aigar.controller.response.Action
import scala.math.round
import com.github.jpbetz.subspace.Vector2
import io.aigar.score.ScoreModification
import scala.collection.mutable.MutableList

class Player(val id: Int, startPosition: Vector2) extends EntityContainer {
  var aiState: AIState = new NullState(this)
  private var currentCellId: Int = 0
  var cells = List(new Cell(currentCellId, this, startPosition))
  var opponents = List[Player]()

  def update(deltaSeconds: Float, grid: Grid, players: List[Player]): Unit = {
    opponents = players diff List(this)
    cells = handleCollision(cells, opponents, None).asInstanceOf[List[Cell]]

    if (shouldRespawn(cells.size, 1)) {
      getRespawnPosition(grid, opponents, Cell.RespawnRetryAttempts) match {
        case Some(position) => {
          currentCellId += 1
          cells = List(new Cell(currentCellId, this, position))
        }
        case _ =>
      }
    }
    cells.foreach { _.update(deltaSeconds, grid) }
  }

  def onCellCollision(opponentCell: Cell,
                      player: Player,
                      entity: Entity,
                      scoreModifications: Option[MutableList[ScoreModification]]): List[Entity] = {
    var entityReturn = List[Entity]()
    val cell = entity.asInstanceOf[Cell]

    if (opponentCell.contains(cell.position) && opponentCell.mass >= Cell.MassDominanceRatio * cell.mass) {
      opponentCell.mass = opponentCell.mass + cell.mass
      entityReturn = List(entity)
    }
    entityReturn
  }

  def shouldRespawn(size: Int, min: Int): Boolean = size < min

  def state: serializable.Player = {
    val mass = round(cells.map(_.mass).sum)
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
        case None =>
      }
    }
  }

  /**
   * Should be called whenever an external action occurs (e.g. we receive a
   * command coming from the AI of a player).
   */
  def onExternalAction: Unit = {
    aiState.onPlayerActivity
  }

  /**
    * The player is active when there is an active cell.
    */
  def isActive(): Boolean = {
    aiState.isActive
  }
}
