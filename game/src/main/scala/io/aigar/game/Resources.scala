package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.score.ScoreModification

object Resource {
  final val RespawnRetryAttempts = 10
}

object Regular {
  final val Max = 75
  final val Min = 0

  // IMPORTANT: keep those values in sync with the client documentation
  final val Mass = 3
  final val Score = 0.1f
}

object Silver {
  final val Max = 0
  final val Min = 0

  // IMPORTANT: keep those values in sync with the client documentation
  final val Mass = 3
  final val Score = 1f
}

object Gold {
  final val Max = 0
  final val Min = 0

  // IMPORTANT: keep those values in sync with the client documentation
  final val Mass = 0
  final val Score = 10f
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

  def update(grid: Grid, players: List[Player]): List[ScoreModification] = {
    var modifications = List[ScoreModification]()
    resourceTypes.foreach { modifications :::= _.update(grid, players) }

    modifications
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
                   resourceScore: Float,
                   resourceMin: Int,
                   resourceMax: Int
                  ) extends EntityContainer {
  // var resources = List.map(resourceMax)(new Resource(grid.randomPosition, resourceMass, resourceScore))
  var resources = List.range(0, resourceMax).map(i => new Resource(Vector2(grid.width / 2f + grid.width * i / 2f / resourceMax, grid.height / 2f + grid.height * i / 2f / resourceMax), resourceMass, resourceScore))

  def update(grid: Grid, players: List[Player]): List[ScoreModification] = {
    val (resourcesReturn, modifications) = handleCollision(resources, players)

    resources = resourcesReturn.asInstanceOf[List[Resource]]

    if (shouldRespawn(resources.size, resourceMin, resourceMax)) {
      getRespawnPosition(grid, players, Resource.RespawnRetryAttempts) match {
        case Some(position) => resources ::= new Resource(position, resourceMass, resourceScore)
        case _ =>
      }
    }
    modifications
  }

  def onCellCollision(cell: Cell,
                      player: Player,
                      entity: Entity): (List[Entity], ScoreModification) = {
    reward(cell, entity.mass)
    (List(entity), new ScoreModification(player.id, entity.scoreModification))
  }

  def randomPosition(grid: Grid): Vector2 = {
    grid.randomPosition
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
               override val _scoreModification: Float = 0.0f) extends Entity {
  _mass = resourceMass

  def radius: Float = 1

  def state: Vector2 = {
    position
  }
}
