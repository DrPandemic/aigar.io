package io.aigar.game

import com.typesafe.scalalogging.LazyLogging
import io.aigar.controller.response.Action
import scala.math.round
import com.github.jpbetz.subspace.Vector2
import io.aigar.score.ScoreModification

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

  var cells: List[Cell] = List()
  spawnCell(startPosition)

  var opponents = List[Player]()

  def update(deltaSeconds: Float, grid: Grid, players: List[Player]): List[ScoreModification] = {
    cells = merge(cells, this)
    opponents = players diff List(this)
    val (cellsReturn, modifications) = handleCollision(cells, opponents)

    cells = cellsReturn.asInstanceOf[List[Cell]]

    if (shouldRespawn(cells.size, 1)) {
      getRespawnPosition(grid, opponents, Cell.RespawnRetryAttempts) match {
        case Some(position) => {
          spawnCell(position)
        }
        case _ =>
      }
    }
    cells.foreach { _.update(deltaSeconds, grid) }
    modifications
  }

  def onCellCollision(opponentCell: Cell,
                      player: Player,
                      entity: Entity): (List[Entity], ScoreModification) = {
    val cell = entity.asInstanceOf[Cell]
    var entitiesReturn = List[Entity]()

    if (opponentCell.contains(cell.position) && opponentCell.mass >= Cell.MassDominanceRatio * cell.mass) {
      logger.info(s"Player ${player.id}'s ${opponentCell.id} (mass ${opponentCell.mass}) ate $id's ${cell.id} (mass ${cell.mass})")
      entitiesReturn :::= List(entity)
      opponentCell.mass = opponentCell.mass + cell.mass
    }
    (entitiesReturn,  ScoreModification(player.id, 0))
  }

  def spawnCell(position: Vector2): Cell = {
    val cell = new Cell(currentCellId, this, position)
    cells ::= cell

    logger.info(s"Player $id respawned with cell ${cell.id} at (${cell.position.x}, ${cell.position.y})")

    currentCellId += 1

    cell
  }

  def shouldRespawn(size: Int, min: Int): Boolean = size < min

  def randomPosition(grid: Grid): Vector2 = {
    grid.randomPosition
  }

  def merge(cells: List[Cell], player: Player): List[Cell] ={
    var cellsReturn = List[Cell]()

    for (cell <- cells) {
      for (secondCell <- cells.filterNot(_.id == cell.id)) {
        if (cell.id != secondCell.id
          && cell.overlaps(secondCell)
          && !cellsReturn.contains(cell)) {
          logger.info(s"Player ${player.id}'s ${cell.id} (mass ${cell.mass}) merged with ${secondCell.id} (mass ${secondCell.mass})")
          cell.mass += secondCell.mass
          cellsReturn ::= secondCell
        }
      }
    }
    cells diff cellsReturn
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

  def performAction(actions: List[Action]): List[ScoreModification] = {
    onExternalAction

    var modifications = List[ScoreModification]()
    actions.foreach {
      action => cells.find(_.id == action.cell_id) match {
        case Some(cell) => cell.performAction(action) match {
          case Some(modification) => modifications :::= List(modification)
          case None =>
        }
        case None =>
      }
    }
    modifications
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
