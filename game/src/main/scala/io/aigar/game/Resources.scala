package io.aigar.game

import io.aigar.score.ScoreMessage

object Regular {
  final val Max = 250
  final val Min = 100
  final val Mass = 1
  final val Score = 1
}

object Silver {
  final val Max = 25
  final val Min = 12
  final val Mass = 3
  final val Score = 3
}

object Gold {
  final val Max = 10
  final val Min = 5
  final val Mass = 0
  final val Score = 10
}

class Resources(grid: Grid) {
  val regular = new ResourceType(grid, Regular.Min, Regular.Max, Regular.Mass, Regular.Score)
  val silver = new ResourceType(grid, Silver.Min, Silver.Max, Silver.Mass, Silver.Score)
  val gold = new ResourceType(grid, Gold.Min, Gold.Max, Gold.Mass, Gold.Score)
  var resourceTypes = List(regular, silver, gold)

  def update(players: List[Player]): List[ScoreMessage] = {
    val scoreMessages = resourceTypes.map(_.detectCollisions(players))
      .reduceLeft(_ ::: _)
    resourceTypes.foreach(_.spawnResources)

    scoreMessages
  }

  def state = {
    serializable.Resources(
      regular.positions,
      silver.positions,
      gold.positions
    )
  }
}

class ResourceType(grid: Grid, val min: Int, val max: Int, mass: Int, score: Int) {
  var positions = List.fill(max)(grid.randomPosition)

  def spawnResources: Unit = {
    val ratio = (positions.length - min).toFloat / (max - min)

    if (scala.util.Random.nextFloat >= ratio) {
      positions :::= List(grid.randomPosition)
    }
  }

  /*
  * Checks if any of the cells from the players should consume a resource.
  * If it is the case, reward the colliding player/cell.
  */
  def detectCollisions(players: List[Player]): List[ScoreMessage] = {
    var scoreMessages = List[ScoreMessage]()
    for(player <- players) {
      for(cell <- player.cells) {
        for(position <- positions){
          if(cell.contains(position)){
            reward(cell)
            scoreMessages = ScoreMessage(player.id, score) :: scoreMessages

            // Returns a new list without the resource that has been consumed
            positions = positions.filterNot(a => a == position)
          }
        }
      }
    }
    scoreMessages
  }

  def reward(cell: Cell): Unit = {
    cell.mass += mass
  }
}
