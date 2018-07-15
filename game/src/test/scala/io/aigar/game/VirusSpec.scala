package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.game.serializable.Position
import org.scalatest.{FlatSpec, Matchers}


class VirusSpec extends FlatSpec with Matchers {
  "A Virus" should "create a state with the right info" in {
    val virus = new Virus(new Vector2(5, 6))

    val state = virus.state

    state.position should equal(new Position(5, 6))
    state.mass should equal(virus.mass)
    state.radius should equal(virus.radius)
  }

  it should "not detect a collision when being into a smaller cell" in {
    val viruses = new Viruses(new Grid(0, 0), 15)
    val virus = new Virus(new Vector2(5, 5))
    val player = new Player(1, new Vector2(5, 5))
    val cell = new Cell(1, player, new Vector2(5, 5))

    viruses.viruses = List(virus)
    // We make sure the cell is small enough so it doesn't eat the virus
    cell.mass = Virus.Mass * Cell.MassDominanceRatio - 1
    player.cells = List(cell)

    viruses.update(new Grid(0, 0), List(player))

    viruses.state should have size 1
  }

  it should "detect a collision when being into a larger cell" in {
    val viruses = new Viruses(new Grid(0, 0), 15)
    val virus = new Virus(new Vector2(5, 5))
    val player = new Player(1, new Vector2(5, 5))
    val cell = new Cell(1, player, new Vector2(5, 5))

    viruses.viruses = List(virus)
    // We make sure the cell is big enough to eat the virus
    cell.mass = Virus.Mass * Cell.MassDominanceRatio + 10
    player.cells = List(cell)

    viruses.update(new Grid(10, 10), List(player))

    viruses.viruses shouldNot contain(virus)
  }

  it should "not respawn on a cell" in {
    val grid = new Grid(1000, 1000)
    val initialPosition = new Vector2(5, 5)
    val viruses = new Viruses(new Grid(0, 0), 15)
    val virus = new Virus(initialPosition)
    val player = new Player(1, initialPosition)
    val cell = new Cell(1, player, initialPosition)

    viruses.viruses = List(virus)
    cell.mass = 200
    player.cells = List(cell)

    viruses.update(grid, List(player))

    viruses.viruses.head.position should not be initialPosition
  }

  "on collision" should "split the cell" in {
    val viruses = new Viruses(new Grid(0, 0), 15)
    val virus = new Virus(new Vector2(5, 5))
    val player = new Player(1, new Vector2(5, 5))
    val cell = new Cell(1, player, new Vector2(5, 5))

    viruses.viruses = List(virus)
    cell.mass = 1000f
    player.cells = List(cell)

    viruses.update(new Grid(10, 10), List(player))

    player.cells should have size 4
  }

  it should "reduce the total mass" in {
    val viruses = new Viruses(new Grid(0, 0), 15)
    val virus = new Virus(new Vector2(5, 5))
    val player = new Player(1, new Vector2(5, 5))
    val cell = new Cell(1, player, new Vector2(5, 5))

    viruses.viruses = List(virus)
    // We make sure the cell is big enough to eat the virus
    cell.mass = Virus.Mass * Cell.MassDominanceRatio + 1
    val oldMass = cell.mass
    player.cells = List(cell)

    viruses.update(new Grid(0, 0), List(player))

    oldMass should be > player.cells.foldLeft(0f)((sum: Float, cell: Cell) => sum + cell.mass)
  }

  it should "still remove enough mass when a player has 9 cells" in {
    val viruses = new Viruses(new Grid(0, 0), 15)
    val virus = new Virus(new Vector2(50, 50))
    val player = new Player(1, new Vector2(5, 5))
    val cell = new Cell(1, player, new Vector2(50, 50))

    viruses.viruses = List(virus)
    cell.mass = 1000f
    player.cells = cell :: (2 to 9).map { i => new Cell(i, player, new Vector2(0,0)) }.toList

    viruses.update(new Grid(100, 100), List(player))

    player.cells should have size 10
    player.cells(0).mass should be > 149f
    player.cells(0).mass should be < 151f
  }

  it should "still remove enough mass when a player has 10 cells" in {
    val viruses = new Viruses(new Grid(0, 0), 15)
    val virus = new Virus(new Vector2(50, 50))
    val player = new Player(1, new Vector2(5, 5))
    val cell = new Cell(1, player, new Vector2(50, 50))

    viruses.viruses = List(virus)
    cell.mass = 1000f
    player.cells = cell :: (2 to 10).map { i => new Cell(i, player, new Vector2(0,0)) }.toList

    viruses.update(new Grid(100, 100), List(player))

    player.cells should have size 10
    player.cells(0).mass should be > 149f
    player.cells(0).mass should be < 151f
  }

  "Viruses" should "create the right number of viruses" in {
    val viruses = new Viruses(new Grid(0, 0), 42)

    viruses.viruses should have size 42
  }
}
