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
    val resources = new Resources(new Grid(0, 0))

    for(resourceType <- resources.resourceTypes) {
      resourceType.positions = resourceType.positions.take(resourceType.min)
    }

    resources.update(List())

    for(resourceType <- resources.resourceTypes) {
      resourceType.positions.length should be > resourceType.min
    }
  }

  it should "not respawn when the quantity is maximal" in {
    val resources = new Resources(new Grid(0, 0))

    for(resourceType <- resources.resourceTypes) {
      resourceType.positions = List.fill(resourceType.max)(new Grid(0, 0).randomPosition)
    }

    resources.update(List())

    for(resourceType <- resources.resourceTypes) {
      resourceType.positions.length should equal(resourceType.max)
    }
  }

  "Resources update" should "return a list of ScoreModification" in {
    val resources = new Resources(new Grid(100, 100))
    resources.regular.positions = List(Vector2(0, 0), Vector2(40, 40))
    resources.silver.positions = List(Vector2(20, 20))
    resources.gold.positions = List(Vector2(40, 40))

    val p1 = new Player(1, resources.regular.positions.head)
    val p2 = new Player(2, resources.silver.positions.head)
    val p3 = new Player(3, resources.gold.positions.head)

    val resourceMessages = resources.update(List(p1, p2, p3))

    resourceMessages should contain allOf (
      ScoreModification(p1.id, Regular.Score),
      ScoreModification(p2.id, Silver.Score),
      ScoreModification(p3.id, Gold.Score),
      ScoreModification(p3.id, Regular.Score)
    )
  }

  "A Resource" should "be consumed on collision" in {
    val resource = new ResourceType(new Grid(0, 0), 0, 0, 5, 10)
    val far = Vector2(1000f, 1000f)
    val player = new Player(1, Vector2(10f, 10f))
    val cell = player.cells.head
    resource.positions = List(Vector2(10f,10f), far)

    resource.detectCollisions(List(player))

    resource.positions should contain only far
  }

  it should "reward the cell accordingly" in {
    val resource = new ResourceType(new Grid(0, 0), 0, 0, 5, 10)
    val player = new Player(1, Vector2(10f, 10f))
    val cell = player.cells.head
    cell.mass = 25

    resource.reward(cell)

    cell.mass should equal(30)
  }

  it should "return a list of ScoreModifications for players that collided" in {
    val resource = new ResourceType(new Grid(0, 0), 0, 0, 5, 10)
    val r1 = Vector2(10f, 10f)
    val r2 = Vector2(50f, 50f)
    resource.positions = List(r1, r2)

    val p1 = new Player(1, Vector2(10f, 10f))
    val p2 = new Player(2, Vector2(50f, 50f))

    val resourceMessages = resource.detectCollisions(List(p1, p2))

    resourceMessages should contain only (ScoreModification(p1.id, 10), ScoreModification(p2.id, 10))
  }

  it should "return an empty list of ScoreModifications when no collision occurs" in {
    val resource = new ResourceType(new Grid(0, 0), 0, 0, 5, 10)
    val r1 = Vector2(10f, 10f)
    val r2 = Vector2(50f, 50f)
    resource.positions = List(r1, r2)

    val p1 = new Player(1, Vector2(100f, 100f))

    val resourceModification = resource.detectCollisions(List(p1))

    resourceModification shouldBe empty
  }
}
