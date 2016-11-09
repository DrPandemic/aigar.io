package io.aigar.game

import com.github.jpbetz.subspace.Vector2


trait EntityContainer {
  def respawn(grid: Grid, players: List[Player], respawnRetryAttempts: Int): Option[Vector2] = {
    var newPosition = grid.randomPosition

    1 to respawnRetryAttempts foreach { _ =>
      for (player <- players) {
        for (cell <- player.cells) {
          if (cell.contains(newPosition))
            newPosition = grid.randomPosition
          else
            return Some(newPosition)
        }
      }
    }
    None
  }
}
