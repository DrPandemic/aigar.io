package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.game.serializable.Position
import io.aigar.game.Vector2Utils.Vector2Addons

object Virus {
  final val Quantity = 15
  final val Mass = 100
  final val RespawnRetryAttempts = 15

  /**
    * Impact on the mass of the cell eating the Virus
    */
  final val ImpactOnMass = 0.75f
}

class Virus(var position: Vector2 = new Vector2(0f, 0f)) extends Entity {
  mass = Virus.Mass

  def state: Position = {
    position.state
  }
}

class Viruses(grid: Grid) extends EntityContainer {
  var viruses = List.fill(Virus.Quantity)(new Virus(grid.randomPosition))

  def shouldRespawn: Boolean = viruses.size < Virus.Quantity

  def update(grid: Grid, players: List[Player]): Unit = {
    viruses = handleCollision(viruses, players).asInstanceOf[List[Virus]]

    if (shouldRespawn) {
      getRespawnPosition(grid, players, Virus.RespawnRetryAttempts) match {
        case Some(position) => viruses :::= List(new Virus(position))
        case _ =>
      }
    }
  }

  def onCellCollision(cell: Cell, entity: Entity): List[Entity] = {
    var entityReturn = List[Entity]()

    if (cell.mass > Virus.Mass * Cell.MassDominanceRatio) {
      cell.mass = cell.mass * Virus.ImpactOnMass
      // TODO Split the cell ;)
      entityReturn = List(entity)
    }
    //Returns the entity to remove from the list
    entityReturn
  }

  def state: List[Position] = {
    viruses.map(_.state)
  }
}
