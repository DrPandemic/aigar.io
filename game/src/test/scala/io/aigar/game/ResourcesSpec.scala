package io.aigar.game

import io.aigar.score._
import com.github.jpbetz.subspace.Vector2
import org.scalatest._

class ResourcesSpec extends FlatSpec with Matchers {
  "Resources" should "spawn at the right quantity" in {
    val resources0 = new Resources(new Grid(0, 0), 30)
    val resources1 = new Resources(new Grid(0, 0), 10)

    val state0 = resources0.state
    val state1 = resources1.state

    state0.regular should have size (Regular.Max * 30).toInt
    state0.silver should have size (Silver.Max * 30).toInt
    state0.gold should have size (Gold.Max * 30).toInt
    state1.regular should have size (Regular.Max * 10).toInt
    state1.silver should have size (Silver.Max * 10).toInt
    state1.gold should have size (Gold.Max * 10).toInt
  }

  it should "respawn when the quantity is minimal" in {
    val grid = new Grid(100000, 1000000)
    val resources = new Resources(grid, 30)

    resources.regulars.resources = resources.regulars.resources.take((Regular.Min * 30).toInt - 1)
    resources.silvers.resources = resources.silvers.resources.take((Silver.Min * 30).toInt - 1)
    resources.golds.resources = resources.golds.resources.take((Gold.Min * 30).toInt - 1)

    resources.update(grid, List(new Player(1, Vector2(0, 0))))

    resources.regulars.resources.size should be >= (Regular.Min * 30).toInt
    resources.silvers.resources.size should be >= (Silver.Min * 30).toInt
    resources.golds.resources.size should be >= (Gold.Min * 30).toInt
  }

  it should "not respawn when the quantity is maximal" in {
    val grid = new Grid(100000, 1000000)
    val resources = new Resources(grid, 30)

    resources.update(grid, List(new Player(1, Vector2(0, 0))))

    resources.regulars.resources should have size (Regular.Max * 30).toInt
    resources.silvers.resources  should have size (Silver.Max * 30).toInt
    resources.golds.resources  should have size (Gold.Max * 30).toInt
  }

  "Resources update" should "return a list of ScoreModification without score when player is inactive" in {
    val grid = new Grid(100, 100)
    val resources = new Resources(new Grid(100, 100), 30)

    val p1 = new Player(1, Vector2(10, 10))
    val p2 = new Player(2, Vector2(20, 20))
    val p3 = new Player(3, Vector2(30, 30))

    resources.regulars.resources = List(
      new Resource(p1.cells.head.position, Regular.Mass, Regular.Score),
      new Resource(p3.cells.head.position, Regular.Mass, Regular.Score))
    resources.silvers.resources = List(new Resource(p2.cells.head.position, Silver.Mass, Silver.Score))
    resources.golds.resources = List(new Resource(p3.cells.head.position, Gold.Mass, Gold.Score))

    val resourceMessages = resources.update(new Grid(0, 0), List(p1, p2, p3))

    resourceMessages should contain allOf (
      ScoreModification(p1.id, 0.0f),
      ScoreModification(p2.id, 0.0f),
      ScoreModification(p3.id, 0.0f)
    )
  }

  "Resources update" should "return a list of ScoreModification when player is active" in {
    val grid = new Grid(100, 100)
    val resources = new Resources(new Grid(100, 100), 30)

    val p1 = new Player(1, Vector2(10, 10))
    p1.active = true
    val p2 = new Player(2, Vector2(20, 20))
    p2.active = true
    val p3 = new Player(3, Vector2(30, 30))
    p3.active = true

    resources.regulars.resources = List(
      new Resource(p1.cells.head.position, Regular.Mass, Regular.Score),
      new Resource(p3.cells.head.position, Regular.Mass, Regular.Score))
    resources.silvers.resources = List(new Resource(p2.cells.head.position, Silver.Mass, Silver.Score))
    resources.golds.resources = List(new Resource(p3.cells.head.position, Gold.Mass, Gold.Score))

    val resourceMessages = resources.update(new Grid(0, 0), List(p1, p2, p3))

    resourceMessages should contain allOf (
      ScoreModification(p1.id, Regular.Score),
      ScoreModification(p2.id, Silver.Score),
      ScoreModification(p3.id, Gold.Score),
      ScoreModification(p3.id, Regular.Score)
    )
  }

  "A Resource" should "be consumed on collision" in {
    val resources = new Resources(new Grid(100, 100), 30)
    val regular = resources.regulars.resources.head
    val player = new Player(1, Vector2(10f, 10f))
    val cell = player.cells.head

    regular.position = cell.position
    resources.update(new Grid(100, 100), List(player))

    resources.regulars.resources should not contain regular
  }

  it should "reward the cell accordingly" in {
    val resources = new Resources(new Grid(100, 100), 30)
    val player = new Player(1, Vector2(10f, 10f))
    val cell = player.cells.head
    val initialMass = 25
    cell.mass = initialMass

    resources.regulars.reward(cell, Regular.Mass)

    cell.mass should equal(initialMass + Regular.Mass)
  }

  "Resources collision" should "return the original list of entities minus the ones that collided with a player" in {
    val resources = new Resources(new Grid(0, 0), 30)
    val p1 = new Player(1, Vector2(10f, 10f))
    val p2 = new Player(2, Vector2(50f, 50f))

    resources.regulars.resources = List(
      new Resource(p1.cells.head.position, Regular.Mass, Regular.Score),
      new Resource(p2.cells.head.position, Regular.Mass, Regular.Score))

    val (resourcesReturn, modifications) = resources.regulars.handleCollision(resources.regulars.resources, List(p1, p2))

    resourcesReturn shouldBe empty
    modifications should contain theSameElementsAs List(ScoreModification(p1.id, 0.0f), ScoreModification(p2.id, 0.0f))
  }

  it should "return the original list of entities when no collision occurs" in {
    val resources = new Resources(new Grid(0, 0), 30)
    val p1 = new Player(1, Vector2(10f, 10f))
    val p2 = new Player(2, Vector2(50f, 50f))

    // Be aware to be out of the radius of the cells
    resources.regulars.resources = List(
      new Resource(Vector2(25, 25), Regular.Mass, Regular.Score),
      new Resource(Vector2(30, 30), Regular.Mass, Regular.Score))

    val (resourcesReturn, modifications) = resources.regulars.handleCollision(resources.regulars.resources, List(p1, p2))

    resourcesReturn should contain theSameElementsAs resources.regulars.resources
    modifications shouldBe empty
  }
}
