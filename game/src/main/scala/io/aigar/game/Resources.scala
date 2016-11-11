package io.aigar.game

import io.aigar.score.ScoreModification

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

class Resources(grid: Grid) extends EntityContainer {
  var regulars = List.fill(Regular.Max)(new Regular(grid.randomPosition))
//  val regular = new ResourceType(grid, Regular.Min, Regular.Max, Regular.Mass, Regular.Score)
//  val silver = new ResourceType(grid, Silver.Min, Silver.Max, Silver.Mass, Silver.Score)
//  val gold = new ResourceType(grid, Gold.Min, Gold.Max, Gold.Mass, Gold.Score)
//  var resourceTypes = List(regular, silver, gold)
//
//  def update(players: List[Player]): List[ScoreModification] = {
//    val scoreModifications = resourceTypes.flatten(_.detectCollisions(players))
//    resourceTypes.foreach(_.spawnResources(players))
//
//    scoreModifications
//  }
//
//  def state = {
//    serializable.Resources(
//      regular.positions,
//      silver.positions,
//      gold.positions
//    )
//  }

  def shouldRespawn: Boolean

  def onCellCollision(cell: Cell, entity: Entity): List[Entity]
}

class Regular(grid: Grid) extends Entity {

}

class Silver extends Entity {

}

class Gold extends Entity {

}

//class ResourceType(grid: Grid, val min: Int, val max: Int, mass: Int, score: Int) {
//  var positions = List.fill(max)(grid.randomPosition)
//
//  def spawnResources(players: List[Player]): Unit = {
//    val ratio = (positions.length - min).toFloat / (max - min)
//
//    if (scala.util.Random.nextFloat >= ratio) {
//      val position = grid.randomPosition
//      for (player <- players) {
//        for (cell <- player.cells) {
//          if (cell.contains(position)) return
//        }
//      }
//      positions :::= List(position)
//    }
//  }
//
//  /*
//  * Checks if any of the cells from the players should consume a resource.
//  * If it is the case, reward the colliding player/cell.
//  */
//  def detectCollisions(players: List[Player]): List[ScoreModification] = {
//    var scoreModifications = List[ScoreModification]()
//    for(player <- players) {
//      for(cell <- player.cells) {
//        for(position <- positions){
//          if(cell.contains(position)){
//            reward(cell)
//            scoreModifications ::= ScoreModification(player.id, score)
//
//            // Returns a new list without the resource that has been consumed
//            positions = positions.filterNot(a => a == position)
//          }
//        }
//      }
//    }
//    scoreModifications
//  }
//
//  def reward(cell: Cell): Unit = {
//    cell.mass += mass
//  }
//}
