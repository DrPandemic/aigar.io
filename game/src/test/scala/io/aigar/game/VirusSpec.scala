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

    cell.mass = 80
    player.cells = List(cell)

    virus.detectCollisions(List(player)) shouldBe false
  }

  it should "detect a collision when being into a larger cell" in {
    val virus = new Virus(new Vector2(5, 5))
    val player = new Player(1, new Vector2(5, 5))
    val cell = new Cell(1, player, new Vector2(5, 5))

    cell.mass = 120
    player.cells = List(cell)

    virus.detectCollisions(List(player)) shouldBe true
  }
}
