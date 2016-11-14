package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.score.ScoreModification
import scala.collection.mutable.MutableList

trait EntityContainer {
  def shouldRespawn(size: Int, min: Int, max: Int): Boolean = {
    val ratio = (size - min).toFloat / (max - min)
    scala.util.Random.nextFloat >= ratio
  }

  def getRespawnPosition(grid: Grid,
                         players: List[Player],
                         respawnRetryAttempts: Int): Option[Vector2] = {
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

  def handleCollision(entities: List[Entity],
                      players: List[Player],
                      scoreModifications: Option[MutableList[ScoreModification]]): List[Entity] ={
    var entitiesReturn = List[Entity]()
    for (entity <- entities){
      for (player <- players) {
        for (cell <- player.cells) {
          if (cell.contains(entity.position)) {
            entitiesReturn :::= onCellCollision(cell, Some(player), entity, scoreModifications)
          }
        }
      }
    }
    entities diff entitiesReturn
  }

  def onCellCollision(cell: Cell,
                      player: Option[Player],
                      entity: Entity,
                      scoreModifications: Option[MutableList[ScoreModification]]): List[Entity]
}
