package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.game.serializable.Position
import org.scalatest.{FlatSpec, Matchers}


class VirusSpec extends FlatSpec with Matchers {
  "A Virus" should "create a state with the right info" in {
    val virus = new Virus(new Vector2(5, 6))

    virus.state should equal(new Position(5, 6))
  }

  it should "not detect a collision when being into a smaller cell" in {
    val virus = new Virus(new Vector2(5, 5))
    val player = new Player(1, new Vector2(5, 5))
    val cell = new Cell(1, player, new Vector2(5, 5))

    // We make sure the cell is small enough so it doesn't eat the virus
    cell.mass = Virus.Mass / Cell.MassDominanceRatio - 1
    player.cells = List(cell)

    virus.detectCollisions(List(player))should equal(None)
  }

  it should "detect a collision when being into a larger cell" in {
    val virus = new Virus(new Vector2(5, 5))
    val player = new Player(1, new Vector2(5, 5))
    val cell = new Cell(1, player, new Vector2(5, 5))

    // We make sure the cell is big enough to eat the virus
    cell.mass = Virus.Mass * Cell.MassDominanceRatio + 1
    player.cells = List(cell)

    virus.detectCollisions(List(player)) should equal(Some(cell))
  }

  it should "not respawn on a cell" in {
    val grid = new Grid(1000, 1000)
    val initialPosition = new Vector2(5, 5)
    val virus = new Virus(initialPosition)
    val player = new Player(1, initialPosition)
    val cell = new Cell(1, player, initialPosition)

    cell.mass = 200
    player.cells = List(cell)

    virus.respawn(grid, List(player))

    virus.position should not be initialPosition
  }
}
