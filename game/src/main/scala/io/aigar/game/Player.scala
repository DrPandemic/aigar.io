package io.aigar.game

import com.typesafe.scalalogging.LazyLogging
import io.aigar.controller.response.Action
import scala.math.round
import com.github.jpbetz.subspace.Vector2
import io.aigar.score.ScoreModification
import scala.collection.mutable.MutableList

object Player {
  /**
   * Maximum amount of cells that a player can control at once.
   * IMPORTANT keep this value in sync with the client documentation.
   */
  final val MaxCells = 10
}

class Player(val id: Int, startPosition: Vector2) extends EntityContainer
                                                  with LazyLogging {
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
          spawnCell(position)
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
      logger.info(s"Player ${player.id}'s ${opponentCell.id} (mass ${opponentCell.mass}) ate $id's ${cell.id} (mass ${cell.mass})")
      opponentCell.mass = opponentCell.mass + cell.mass
      entityReturn = List(entity)
    }
    entityReturn
  }

  def spawnCell(position: Vector2): Cell = {
    currentCellId += 1

    val cell = new Cell(currentCellId, this, position)
    cells ::= cell

    logger.info(s"Player $id respawned with cell ${cell.id} at (${cell.position.x}, ${cell.position.y})")

    cell
  }

  def shouldRespawn(size: Int, min: Int): Boolean = size < min

  def randomPosition(grid: Grid): Vector2 = {
    grid.randomPosition
  }

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
    if (!isActive) logger.info(s"Player $id reconnected.")
    aiState.onPlayerActivity
  }

  /**
    * The player is active when there is an active cell.
    */
  def isActive(): Boolean = {
    aiState.isActive
  }
}
