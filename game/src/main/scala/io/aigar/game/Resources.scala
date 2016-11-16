package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.score.ScoreModification
import scala.collection.mutable.MutableList

object Resource {
  final val RespawnRetryAttempts = 10
}

object Regular {
  final val Max = 250
  final val Min = 100

  // IMPORTANT: keep those values in sync with the client documentation
  final val Mass = 1
  final val Score = 1
}

object Silver {
  final val Max = 25
  final val Min = 12

  // IMPORTANT: keep those values in sync with the client documentation
  final val Mass = 3
  final val Score = 3
}

object Gold {
  final val Max = 10
  final val Min = 5

  // IMPORTANT: keep those values in sync with the client documentation
  final val Mass = 0
  final val Score = 10
}

class Resources(grid: Grid) {
  val regulars = new ResourceType(
    grid,
    Regular.Mass,
    Regular.Score,
    Regular.Min,
    Regular.Max
  )
  val silvers = new ResourceType(
    grid,
    Silver.Mass,
    Silver.Score,
    Silver.Min,
    Silver.Max
  )
  val golds = new ResourceType(
    grid,
    Gold.Mass,
    Gold.Score,
    Gold.Min,
    Gold.Max
  )
  val resourceTypes = List(regulars, silvers, golds)

  def update(grid: Grid, players: List[Player]): MutableList[ScoreModification] = {
    val scoreModifications = MutableList[ScoreModification]()
    resourceTypes.foreach { _.update(grid, players, scoreModifications) }

    scoreModifications
  }

  def state: serializable.Resources = {
    serializable.Resources(
      regulars.state,
      silvers.state,
      golds.state
    )
  }
}

class ResourceType(grid: Grid,
                   resourceMass: Float,
                   resourceScore: Int,
                   resourceMin: Int,
                   resourceMax: Int
                  ) extends EntityContainer {
  var resources = List.fill(resourceMax)(new Resource(grid.randomPosition, resourceMass, resourceScore))

  def update(grid: Grid, players: List[Player], scoreModifications: MutableList[ScoreModification]): Unit = {
    resources = handleCollision(resources, players, Some(scoreModifications)).asInstanceOf[List[Resource]]

    if (shouldRespawn(resources.size, resourceMin, resourceMax)) {
      getRespawnPosition(grid, players, Resource.RespawnRetryAttempts) match {
        case Some(position) => resources :::= List(new Resource(position, resourceMass, resourceScore))
        case _ =>
      }
    }
  }

  def onCellCollision(cell: Cell,
                      player: Player,
                      entity: Entity,
                      scoreModifications: Option[MutableList[ScoreModification]]): List[Entity] = {
    scoreModifications.get += ScoreModification(player.id, entity.scoreModification)
    reward(cell, entity.mass)
    List(entity)
  }

  def reward(cell: Cell, mass: Float): Unit = {
    cell.mass += mass
  }

  def state: List[Vector2] = {
    resources.map(_.state)
  }
}

class Resource(var position: Vector2 = new Vector2(0f, 0f),
               val resourceMass: Float,
               val scoreModification: Int = 0) extends Entity {
  _mass = resourceMass

  def radius: Float = 1

  def state: Vector2 = {
    position
  }
}
