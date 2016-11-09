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

  def update(grid: Grid, players: List[Player]): Unit = {

    detectCollisions(players)

    if(viruses.size < Virus.Quantity) {
      val position = respawn(grid, players, Virus.RespawnRetryAttempts)

      position match {
        case Some(position) => viruses :::= List(new Virus(position))
        case _ =>
      }
    }
  }

  def detectCollisions(players: List[Player]): Option[Cell] ={
    for(virus <- viruses){
      for(player <- players) {
        for(cell <- player.cells) {
          if(cell.contains(virus.position) && cell.mass > Virus.Mass * Cell.MassDominanceRatio){
            // TODO : Split cell
            viruses = viruses diff List(virus)
            return Some(cell)
          }
        }
      }
    }
    None
  }

  def state: List[Position] = {
    viruses.map(_.state)
  }
}
