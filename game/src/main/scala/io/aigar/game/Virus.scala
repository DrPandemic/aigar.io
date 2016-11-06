package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.game.serializable.Position

object Virus {
  final val Quantity = 15
  final val Mass = 100
}

class Virus(var position: Vector2) {
  def update(grid: Grid, players: List[Player]): Unit = {
    if(detectCollisions(players)) {
      // TODO Add the virus consumption into the cell
      respawn(grid, players)
    }
  }

  def detectCollisions(players: List[Player]): Boolean ={
    for(player <- players) {
      for(cell <- player.cells) {
        // TODO Change the 1.1 value to the constant
        if(cell.contains(position) && cell.mass > Virus.Mass * 1.1){
          return true
        }
      }
    }
    false
  }

  def respawn(grid: Grid, players: List[Player]): Unit = {
    val listPositions = players.map { _.cells.map { _.position} }
    var newPosition = grid.randomPosition

    1 to 15 foreach { _ =>
      if (listPositions.contains(newPosition)) {
        newPosition = grid.randomPosition
      }
    }
    position = newPosition
  }

  def state: Position = {
    Vector2Utils.StateAddon(position).state
  }
}
