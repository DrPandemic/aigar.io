package io.aigar.game

import com.github.jpbetz.subspace.Vector2

object Regular {
  final val Max = 250
  final val Min = 100
  final val Mass = 1
  final val Score = 0
}

object Silver {
  final val Max = 25
  final val Min = 12
  final val Mass = 3
  final val Score = 0
}

object Gold {
  final val Max = 10
  final val Min = 5
  final val Mass = 0
  final val Score = 3
}

class Resources(grid: Grid) {
  val regular = new ResourceType(grid, Regular.Min, Regular.Max, Regular.Mass, Regular.Score)
  val silver = new ResourceType(grid, Silver.Min, Silver.Max, Silver.Mass, Silver.Score)
  val gold = new ResourceType(grid, Gold.Min, Gold.Max, Gold.Mass, Gold.Score)
  var resourceTypes = List(regular, silver, gold)

  def update(players: List[Player]): Unit = {
    resourceTypes.foreach(_.detectCollision(players))
    resourceTypes.foreach(_.spawnResources)
  }

  def state = {
    serializable.Resources(
      regular.positions,
      silver.positions,
      gold.positions
    )
  }
}

class ResourceType(grid:Grid, val min: Int, val max: Int, mass: Int, score: Int) {
  var positions = List.fill(max)(grid.randomPosition)

  def spawnResources: Unit = {
    val ratio = (positions.length - min).toFloat / (max - min)
    if (scala.util.Random.nextFloat >= ratio) {
      positions :::= List(grid.randomPosition)
    }
  }

  def detectCollision(players: List[Player]): Unit = {
    for(player <- players) {
      for(cell <- player.cells) {
        for(position <- positions){
          if(cell.contains(position)){
            reward(player, cell, mass, score)
            positions = positions.filterNot(a => a == position)
          }
        }
      }
    }
  }

  def reward(player: Player, cell: Cell, mass: Int, score: Int): Unit = {
    cell.mass += mass
    /*player.score += score*/
  }
}