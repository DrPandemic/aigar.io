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

  it should "move its cells on update" in {
    val player = new Player(0, new Vector2(0f, 0f))
    val target = new Vector2(100f, 100f)
    player.cells.head.target = target
    val grid = new Grid(100, 100)

    val initialDistance = player.cells.head.position.distanceTo(target)

    player.update(1f, grid, List(new Player(0, new Vector2(0, 0))))

    val finalDistance = player.cells.head.position.distanceTo(target)

    initialDistance should be > finalDistance
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
    player.aiState = new TestState

    player.onExternalAction

    player.aiState shouldBe 'active
  }

  it should "remove a cell from its list when it is dead" in {
    val player = new Player(1, new Vector2(0f, 0f))
    val cell1 = new Cell(1, player)
    val cell2 = new Cell(2, player)

    player.cells = List(cell1, cell2)

    player.removeCell(cell1)

    player.cells should contain only cell2
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
    player.aiState = new TestState

    player.update(NullState.MaxInactivitySeconds * 0.9f, new Grid(0, 0), List(player))
    player.performAction(List(Action(0, false, false, 0, Position(0f, 10f))))
    player.update(NullState.MaxInactivitySeconds * 0.9f, new Grid(0, 0), List(player))

    player.aiState shouldBe 'active
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

  "A Player's cell" should "be eaten when it is smaller than 90% of the opponent mass" in {
    val player = new Player(0, Vector2(10f, 10f))
    val smallCell = player.cells.head
    val opponent = new Player(2, Vector2(10f, 10f))
    val largeCell = opponent.cells.head

    smallCell.mass = 26
    largeCell.mass = 30

    //The return is the entity to remove, hence the cell of the player if applicable
    player.onCellCollision(opponent.cells.head, player.cells.head) should contain (smallCell.asInstanceOf[Entity])
  }

  it should "not be eaten by a cell between 90% to 100% of its mass" in {
    val player = new Player(0, Vector2(10f, 10f))
    val largeCell = player.cells.head
    val opponent = new Player(2, Vector2(10f, 10f))
    val smallCell = opponent.cells.head

    largeCell.mass = Cell.MinMass + 1
    smallCell.mass = Cell.MinMass

    //The return is the entity to remove, hence the cell of the player if applicable
    player.onCellCollision(opponent.cells.head, player.cells.head) shouldBe empty
  }

  it should "not be eaten by a smaller cell" in {
    val player = new Player(0, Vector2(10f, 10f))
    val smallCell = player.cells.head
    val opponent = new Player(2, Vector2(10f, 10f))
    val largeCell = opponent.cells.head

    largeCell.mass = Cell.MinMass + 1
    smallCell.mass = Cell.MinMass

    //The return is the entity to remove, hence the cell of the player if applicable
    player.onCellCollision(opponent.cells.head, player.cells.head) shouldBe empty
  }
}
