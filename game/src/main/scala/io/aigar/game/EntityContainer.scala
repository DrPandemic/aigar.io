package io.aigar.game

import com.github.jpbetz.subspace.Vector2

trait EntityContainer {
  def shouldRespawn: Boolean

  def getRespawnPosition(grid: Grid, players: List[Player], respawnRetryAttempts: Int): Option[Vector2] = {
    1 to respawnRetryAttempts foreach { _ =>
      var collides = false
      val newPosition = grid.randomPosition
      for (player <- players) {
        for (cell <- player.cells) {
          if (cell.contains(newPosition)) collides = true
        }
      }
      if (!collides) return Some(newPosition)
    }
    None
  }

  def handleCollision(entities: List[Entity], players: List[Player]): List[Entity] ={
    var entitiesReturn = List[Entity]()
    for (entity <- entities){
      for (player <- players) {
        for (cell <- player.cells) {
          if (cell.contains(entity.position)) {
            entitiesReturn :::= onCellCollision(cell, entity)
          }
        }
      }
    }
    entities diff entitiesReturn
  }

  def onCellCollision(cell: Cell, entity: Entity): List[Entity]
}
