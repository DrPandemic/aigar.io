import io.aigar.game._
import io.aigar.controller.response.Action
import io.aigar.game.serializable.Position
import org.scalatest._
import org.scalatest.LoneElement._
import com.github.jpbetz.subspace.Vector2

class PlayerSpec extends FlatSpec with Matchers {
  "A Player" should "start with a cell at its start position" in {
    val player = new Player(0, new Vector2(42f, 42f))

    player.cells.loneElement.position should equal(new Vector2(42f, 42f))
  }

  it should "move its cells on updates" in {
    val player = new Player(0, new Vector2(0f, 0f))
    val target = new Vector2(100f, 100f)
    val cell = player.cells.head
    cell.aiState = new NullState(cell)
    cell.target = target
    val grid = new Grid(100, 100)

    player.update(1f, grid, List(player))

    player.cells.head.velocity.magnitude should be > 0f
  }

  it should "generate a state with the right info" in {
    val player = new Player(1, new Vector2(0f, 0f))
    player.cells = List(new Cell(1, player), new Cell(2, player))
    player.cells(0).mass = Cell.MinMass
    player.cells(1).mass = 25

    val state = player.state

    state.total_mass should equal(25 + Cell.MinMass)
    state.id should equal(1)
    state.cells should have size 2
  }

  it should "execute state callbacks when calling the external action callback" in {
    val player = new Player(1, new Vector2(0f, 0f))
    player.cells = List(new Cell(1, player), new Cell(2, player))
    val cell = player.cells.head
    cell.aiState = new TestState(cell)

    player.onExternalAction

    player shouldBe 'active
  }

  it should "merge cells when they overlap" in {
    val player = new Player(1, Vector2(10f, 10f))
    player.cells = List(new Cell(1, player), new Cell(2, player))

    player.cells = player.merge(player.cells, player)

    player.cells should have size 1
    player.cells.head.mass shouldBe (2 * Cell.MinMass)
  }

  it should "not merge cells when they don't overlap" in {
    val player = new Player(1, Vector2(10f, 10f))
    player.cells = List(new Cell(1, player), new Cell(2, player))
    player.cells.head.position = Vector2(30f, 30f)

    player.cells = player.merge(player.cells, player)

    player.cells should have size 2
    player.cells.head.mass shouldBe Cell.MinMass
    player.cells(1).mass shouldBe Cell.MinMass
  }

  "performAction" should "update cell's targets" in {
    val player = new Player(1, new Vector2(0f, 0f))
    player.cells = List(new Cell(0, player), new Cell(1, player))
    player.performAction(List(
                    Action(0, false, false, 0, Position(0f, 10f)),
                    Action(1, false, false, 0, Position(10f, 15f))))

    player.state.cells.find(_.id == 0).get.target should equal(Position(0f, 10f))
    player.state.cells.find(_.id == 1).get.target should equal(Position(10f, 15f))
  }

  it should "prevent player from going wandering" in {
    val player = new Player(1, new Vector2(0f, 0f))
    player.cells = List(new Cell(1, player), new Cell(2, player))
    player.cells.head.aiState = new TestState(player.cells.head)

    player.update(NullState.MaxInactivitySeconds * 0.9f, new Grid(0, 0), List(player))
    player.performAction(List(Action(0, false, false, 0, Position(0f, 10f))))
    player.update(NullState.MaxInactivitySeconds * 0.9f, new Grid(0, 0), List(player))

    player shouldBe 'active
  }

  "update" should "respawn one cell for itself if it has no mo' cell" in {
    val player = new Player(1, new Vector2(0f, 0f))
    val opponent = new Player(2, new Vector2(0f, 0f))
    val cell1 = new Cell(1, player)
    val cell2 = new Cell(2, player)

    cell2.mass = 2 * Cell.MinMass
    opponent.cells = List(cell2)
    player.cells = List(cell1)

    player.update(1f, new Grid(100, 100), List(player, opponent))

    player.cells.size should equal(1)
    player.cells.head should not be theSameInstanceAs(cell1)
  }

  it should "spawn cell with never used id" in {
    val player = new Player(1, new Vector2(0f, 0f))
    val initialCellId = player.cells.head.id

    player.cells = List()
    player.update(1f, new Grid(0, 0), List(player))

    player.cells should not be empty
    player.cells.map(_.id) should not contain initialCellId
  }
}
