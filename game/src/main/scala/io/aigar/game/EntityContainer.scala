package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.score.ScoreModification

trait EntityContainer {
  def shouldRespawn(size: Int, min: Int, max: Int): Boolean = {
    return false
    val ratio = (size - min).toFloat / (max - min)
    scala.util.Random.nextFloat >= ratio
  }

  def randomPosition(grid: Grid): Vector2

  def getRespawnPosition(grid: Grid,
                         players: List[Player],
                         respawnRetryAttempts: Int): Option[Vector2] = {
    1 to respawnRetryAttempts foreach { _ =>
      var collides = false
      val newPosition = randomPosition(grid)
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
                      players: List[Player]): (List[Entity], List[ScoreModification]) = {
    var entitiesReturn = List[Entity]()
    var modifications = List[ScoreModification]()

    for (entity <- entities){
      for (player <- players) {
        for (cell <- player.cells) {
          if (cell.overlaps(entity)) {
            val (entitiesToRemove, modificationsToAdd) = onCellCollision(cell, player, entity)
            entitiesReturn :::= entitiesToRemove
            modifications ::= modificationsToAdd
          }
        }
      }
    }
    (entities diff entitiesReturn, modifications)
  }

  def onCellCollision(cell: Cell,
                      player: Player,
                      entity: Entity): (List[Entity], ScoreModification)
}
