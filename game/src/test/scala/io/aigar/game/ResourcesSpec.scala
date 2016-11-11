package io.aigar.game

import io.aigar.score._
import com.github.jpbetz.subspace.Vector2
import org.scalatest._

class ResourcesSpec extends FlatSpec with Matchers {
  "Resources" should "spawn at the right quantity" in {
    val resources = new Resources(new Grid(0, 0))

    val state = resources.state

    state.regular should have size Regular.Max
    state.silver should have size Silver.Max
    state.gold should have size Gold.Max
  }

  it should "respawn when the quantity is minimal" in {
    val grid = new Grid(100, 100)
    val resources = new Resources(grid)

    resources.regulars = resources.regulars.take(Regular.Min)
    resources.silvers = resources.silvers.take(Silver.Min)
    resources.golds = resources.golds.take(Gold.Min)

    resources.update(grid, List(new Player(1, new Vector2(0, 0))))

    resources.regulars.size should be > Regular.Min
    resources.silvers.size should be > Silver.Min
    resources.golds.size should be > Gold.Min
  }

  it should "not respawn when the quantity is maximal" in {
    val grid = new Grid(100, 100)
    val resources = new Resources(grid)

    resources.update(grid, List(new Player(1, new Vector2(0, 0))))

    resources.regulars should have size Regular.Max
    resources.silvers should have size Silver.Max
    resources.golds should have size Gold.Max
  }

//  "Resources update" should "return a list of ScoreModification" in {
//    val resources = new Resources(new Grid(100, 100))
//    resources.regular.positions = List(Vector2(0, 0), Vector2(40, 40))
//    resources.silver.positions = List(Vector2(20, 20))
//    resources.gold.positions = List(Vector2(40, 40))
//
//    val p1 = new Player(1, resources.regular.positions.head)
//    val p2 = new Player(2, resources.silver.positions.head)
//    val p3 = new Player(3, resources.gold.positions.head)
//
//    val resourceMessages = resources.update(new Grid(0, 0), List(p1, p2, p3))
//
//    resourceMessages should contain allOf (
//      ScoreModification(p1.id, Regular.Score),
//      ScoreModification(p2.id, Silver.Score),
//      ScoreModification(p3.id, Gold.Score),
//      ScoreModification(p3.id, Regular.Score)
//    )
//  }

  "A Resource" should "be consumed on collision" in {
    val resources = new Resources(new Grid(100, 100))
    val regular = resources.regulars.head
    val player = new Player(1, Vector2(10f, 10f))
    val cell = player.cells.head

    regular.position = cell.position
    resources.update(new Grid(100, 100), List(player))

    resources.regulars should not contain regular
  }

  it should "reward the cell accordingly" in {
    val resources = new Resources(new Grid(100, 100))
    val player = new Player(1, Vector2(10f, 10f))
    val cell = player.cells.head
    val initialMass = 25
    cell.mass = initialMass

    resources.reward(cell, Regular.Mass)

    cell.mass should equal(initialMass + Regular.Mass)
  }

//  it should "return a list of ScoreModifications for players that collided" in {
//    val resource = new ResourceType(new Grid(0, 0), 0, 0, 5, 10)
//    val r1 = Vector2(10f, 10f)
//    val r2 = Vector2(50f, 50f)
//    resource.positions = List(r1, r2)
//
//    val p1 = new Player(1, Vector2(10f, 10f))
//    val p2 = new Player(2, Vector2(50f, 50f))
//
//    val resourceMessages = resource.detectCollisions(List(p1, p2))
//
//    resourceMessages should contain only (ScoreModification(p1.id, 10), ScoreModification(p2.id, 10))
//  }
//
//  it should "return an empty list of ScoreModifications when no collision occurs" in {
//    val resource = new ResourceType(new Grid(0, 0), 0, 0, 5, 10)
//    val r1 = Vector2(10f, 10f)
//    val r2 = Vector2(50f, 50f)
//    resource.positions = List(r1, r2)
//
//    val p1 = new Player(1, Vector2(100f, 100f))
//
//    val resourceModification = resource.detectCollisions(List(p1))
//
//    resourceModification shouldBe empty
//  }
}
