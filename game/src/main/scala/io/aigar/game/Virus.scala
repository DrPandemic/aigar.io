package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.game.serializable.Position
import io.aigar.game.Vector2Utils.StateAddon

object Virus {
  final val Quantity = 15
  final val Mass = 100
  final val RespawnRetryAttempts = 15
}

class Virus(spawnPosition: Vector2) extends Entity {
  position = spawnPosition
  mass = Virus.Mass

  def state: Position = {
    position.state
  }
}

class Viruses(grid: Grid) extends EntityContainer {
  var viruses = List.fill(Virus.Quantity)(new Virus(grid.randomPosition))

  def shouldRespawn: Boolean = viruses.size < Virus.Quantity

  def update(grid: Grid, players: List[Player]): Unit = {
    viruses = detectCollisions(viruses, players).asInstanceOf[List[Virus]]

    if (shouldRespawn) {
      getRespawnPosition(grid, players, Virus.RespawnRetryAttempts) match {
        case Some(position) => viruses :::= List(new Virus(position))
        case _ =>
      }
    }
  }

  def state: List[Position] = {
    viruses.map(_.state)
  }
}
