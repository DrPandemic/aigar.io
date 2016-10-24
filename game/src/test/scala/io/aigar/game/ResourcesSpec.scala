package io.aigar.game

import org.scalatest._

class ResourcesSpec extends FlatSpec with Matchers {
  "Resources" should "spawn at the right quantity" in {
    val resources = new Resources(new Grid(0, 0))

    val state = resources.state

    state.regular should have size Regular.Max
    state.silver should have size Silver.Max
    state.gold should have size Gold.Max
  }

  it should "respawn resources when the quantity is minimal" in {
    val resources = new Resources(new Grid(0, 0))

    for(resourceType <- resources.resourceTypes){
      resourceType.positions = resourceType.positions.take(resourceType.min)
    }

    resources.update(List())

    for(resourceType <- resources.resourceTypes){
      resourceType.positions.length should be > resourceType.min
    }
  }

  it should "not respawn resources when the quantity is maximal" in {
    val resources = new Resources(new Grid(0, 0))

    for(resourceType <- resources.resourceTypes){
      resourceType.positions = List.fill(resourceType.max)(new Grid(0, 0).randomPosition)
    }

    resources.update(List())

    for(resourceType <- resources.resourceTypes){
      resourceType.positions.length should equal(resourceType.max)
    }
  }

  "Cell" should "increase its mass when eating regular or silver resources" in {
    val game = new Game(0, 1 to 15 toList)
    val resources = new Resources(game.grid)
    val cell = game.players.head.cells.head
    val initialMass = cell.mass

    resources.resourceTypes.head.reward(cell)

    cell.mass should be > initialMass
  }
}
