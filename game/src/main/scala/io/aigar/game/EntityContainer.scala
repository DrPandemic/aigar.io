package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.score.ScoreModification
import scala.collection.mutable.MutableList

trait EntityContainer {
  def shouldRespawn(size: Int, min: Int): Boolean = size < min

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

  def handleCollision(entities: List[Entity], players: List[Player], scoreModifications: MutableList[ScoreModification]): List[Entity] ={
    var entitiesReturn = List[Entity]()
    for (entity <- entities){
      for (player <- players) {
        for (cell <- player.cells) {
          if (cell.contains(entity.position)) {
            entitiesReturn :::= onCellCollision(cell, player, entity, scoreModifications)
          }
        }
      }
    }
    entities diff entitiesReturn
  }

  def onCellCollision(cell: Cell, player: Player, entity: Entity, scoreModifications: MutableList[ScoreModification]): List[Entity]
}
