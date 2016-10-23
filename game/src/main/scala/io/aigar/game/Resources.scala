package io.aigar.game

object Regular {
  final val Max = 250
  final val Min = 100
  final val Mass = 1
  final val Score = 0
}

object Silver {
  final val Max = 100
  final val Min = 50
  final val Mass = 3
  final val Score = 0
}

object Gold {
  final val Max = 50
  final val Min = 25
  final val Mass = 0
  final val Score = 3
}

class Resources(grid: Grid) {
  val regular = new ResourceType(grid, Regular.Min, Regular.Max, Regular.Mass, Regular.Score)
  val silver = new ResourceType(grid, Silver.Min, Silver.Max, Silver.Mass, Silver.Score)
  val gold = new ResourceType(grid, Gold.Min, Gold.Max, Gold.Mass, Gold.Score)
  var resourceTypes = List(regular, silver, gold)

  def update: Unit = {
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
}