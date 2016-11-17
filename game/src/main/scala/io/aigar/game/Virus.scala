package io.aigar.game

import scala.math.round
import com.typesafe.scalalogging.LazyLogging
import com.github.jpbetz.subspace.Vector2
import io.aigar.game.serializable.Position
import io.aigar.score.ScoreModification
import io.aigar.game.Vector2Utils.Vector2Addons

object Virus {
  final val Max = 15
  final val Min = 10
  final val Mass = 100
  final val RespawnRetryAttempts = 15

  /**
    * Impact on the mass of the cell eating the Virus
    */
  final val ImpactOnMass = 0.75f
}

class Virus(var position: Vector2 = new Vector2(0f, 0f)) extends Entity {
  _mass = Virus.Mass
  val scoreModification = 0
  def radius: Float = Cell.radius(mass)

  def state: serializable.Virus = {
    serializable.Virus(position.state,
                       round(mass),
                       round(radius))
  }
}

class Viruses(grid: Grid) extends EntityContainer
                          with LazyLogging {
  var viruses = List.fill(Virus.Max)(new Virus(grid.randomPosition))

  def update(grid: Grid, players: List[Player]): List[ScoreModification] = {
    val tupleReturn = handleCollision(viruses, players)

    viruses = tupleReturn._1.asInstanceOf[List[Virus]]
    val modifications = tupleReturn._2

    if (shouldRespawn(viruses.size, Virus.Min, Virus.Max)) {
      getRespawnPosition(grid, players, Virus.RespawnRetryAttempts) match {
        case Some(position) => viruses ::= new Virus(position)
        case _ =>
      }
    }
    modifications
  }

  def onCellCollision(cell: Cell,
                      player: Player,
                      entity: Entity): ScoreModification = {
    if (cell.mass > Virus.Mass * Cell.MassDominanceRatio) {
      logger.info(s"Player ${player.id}'s ${cell.id} (mass ${cell.mass}) ate a virus.")

      cell.mass = cell.mass * Virus.ImpactOnMass
      // TODO Split the cell ;)
    }
    new ScoreModification(player.id, 0)
  }

  def randomPosition(grid: Grid): Vector2 = {
    grid.randomRadiusPosition
  }

  def state: List[serializable.Virus] = {
    viruses.map(_.state)
  }
}
