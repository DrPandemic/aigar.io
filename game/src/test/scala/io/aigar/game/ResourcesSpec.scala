package io.aigar.game

import io.aigar.score._
import com.github.jpbetz.subspace.Vector2
import org.scalatest._
import scala.collection.mutable.MutableList

class ResourcesSpec extends FlatSpec with Matchers {
  "Resources" should "spawn at the right quantity" in {
    val resources = new Resources(new Grid(0, 0))

    val state = resources.state

    state.regular should have size Regular.Max
    state.silver should have size Silver.Max
    state.gold should have size Gold.Max
  }

  it should "respawn when the quantity is minimal" in {
    val grid = new Grid(100000, 1000000)
    val resources = new Resources(grid)

    resources.regulars = resources.regulars.take(Regular.Min - 1)
    resources.silvers = resources.silvers.take(Silver.Min - 1)
    resources.golds = resources.golds.take(Gold.Min - 1)

    resources.update(grid, List(new Player(1, Vector2(0, 0))))

    resources.regulars.size should be >= Regular.Min
    resources.silvers.size should be >= Silver.Min
    resources.golds.size should be >= Gold.Min
  }

  it should "not respawn when the quantity is maximal" in {
    val grid = new Grid(100000, 1000000)
    val resources = new Resources(grid)

    resources.update(grid, List(new Player(1, Vector2(0, 0))))

    resources.regulars should have size Regular.Max
    resources.silvers should have size Silver.Max
    resources.golds should have size Gold.Max
  }

  "Resources update" should "return a list of ScoreModification" in {
    val grid = new Grid(100, 100)
    val resources = new Resources(new Grid(100, 100))

    val p1 = new Player(1, Vector2(10, 10))
    val p2 = new Player(2, Vector2(20, 20))
    val p3 = new Player(3, Vector2(30, 30))

    resources.regulars = List(
      new Regular(p1.cells.head.position),
      new Regular(p3.cells.head.position))
    resources.silvers = List(new Silver(p2.cells.head.position))
    resources.golds = List(new Gold(p3.cells.head.position))

    val resourceMessages = resources.update(new Grid(0, 0), List(p1, p2, p3))

    resourceMessages should contain allOf (
      ScoreModification(p1.id, Regular.Score),
      ScoreModification(p2.id, Silver.Score),
      ScoreModification(p3.id, Gold.Score),
      ScoreModification(p3.id, Regular.Score)
    )
  }

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

  "Resources collision" should "return the original list of entities minus the ones that collided with a player" in {
    val resources = new Resources(new Grid(0, 0))
    val p1 = new Player(1, Vector2(10f, 10f))
    val p2 = new Player(2, Vector2(50f, 50f))

    resources.regulars = List(
      new Regular(p1.cells.head.position),
      new Regular(p2.cells.head.position))

    val regularsReturn = resources.handleCollision(
      resources.regulars,
      List(p1, p2),
      Some(new MutableList[ScoreModification]()))

    regularsReturn shouldBe empty
  }

  it should "return the original list of entities when no collision occurs" in {
    val resources = new Resources(new Grid(0, 0))
    val p1 = new Player(1, Vector2(10f, 10f))
    val p2 = new Player(2, Vector2(50f, 50f))

    // Be aware to be out of the radius of the cells
    resources.regulars = List(
      new Regular(Vector2(25, 25)),
      new Regular(Vector2(30, 30)))

    val regularsReturn = resources.handleCollision(
      resources.regulars,
      List(p1, p2),
      Some(new MutableList[ScoreModification]()))

    regularsReturn should contain theSameElementsAs resources.regulars
  }
}
