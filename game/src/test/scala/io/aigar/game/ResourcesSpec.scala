package io.aigar.game

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

    for(resourceType <- resources.resourceTypes){
      resourceType.positions = resourceType.positions.take(resourceType.min)
    }

    resources.update(List())

    for(resourceType <- resources.resourceTypes){
      resourceType.positions.length should be > resourceType.min
    }
  }

  it should "not respawn when the quantity is maximal" in {
    val resources = new Resources(new Grid(0, 0))

    for(resourceType <- resources.resourceTypes){
      resourceType.positions = List.fill(resourceType.max)(new Grid(0, 0).randomPosition)
    }

    resources.update(List())

    for(resourceType <- resources.resourceTypes){
      resourceType.positions.length should equal(resourceType.max)
    }
  }

  "A Resource" should "be consumed on collision" in {
    val resource = new ResourceType(new Grid(0, 0), 0,0,5,10)
    val far = Vector2(1000f, 1000f)
    resource.positions = List(Vector2(10f,10f), far)
    val cell = new Cell(1)
    cell.position = Vector2(10f, 10f)
    val player = new Player(1, Vector2(10f, 10f))
    player.cells = List(cell)

    resource.detectCollisions(List(player))

    resource.positions should contain only far
  }

  it should "reward the cell accordingly" in {
    val resource = new ResourceType(new Grid(0, 0), 0, 0, 5, 10)
    val cell = new Cell(1)
    cell.mass = 25

    resource.reward(cell)

    cell.mass should equal(30)
  }
}
