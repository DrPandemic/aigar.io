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
    val newPosition = grid.randomPosition

    // We always want 15 viruses. How to we manage it without doing a while true loop?
    for (player <- players) {
      for (cell <- player.cells) {
        if (cell.contains(newPosition)) return
      }
    }

    position = newPosition
  }

  def state: Position = {
    Vector2Utils.StateAddon(position).state
  }
}
