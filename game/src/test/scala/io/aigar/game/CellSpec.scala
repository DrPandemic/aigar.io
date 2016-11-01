import io.aigar.game._
import io.aigar.game.serializable.Position
import io.aigar.game.Vector2Utils._
import io.aigar.controller.response.Action
import org.scalatest._
import com.github.jpbetz.subspace._
import scala.math._

class CellSpec extends FlatSpec with Matchers {
  "A Cell" should "not initiate movement when its target is on itself" in {
    val cell = new Cell(1, new Vector2(42f, 42f))
    cell.target = new Vector2(42f, 42f)
    cell.behavior = new NoBehavior(cell)
    val grid = new Grid(100, 100);

    cell.update(1f, grid)

    cell.position should equal(new Vector2(42f, 42f))
  }

  it should "move towards its target when it is away from itself" in {
    val cell = new Cell(1, new Vector2(42f, 42f))
    cell.target = new Vector2(1000f, 1000f)
    val grid = new Grid(100, 100);

    val initialDistance = cell.position.distanceTo(cell.target)

    cell.update(1f, grid)

    val finalDistance = cell.position.distanceTo(cell.target)

    initialDistance should be > finalDistance
  }

  it should "have a maximal velocity" in {
    val cell = new Cell(1)

    val hugeVelocity = new Vector2(1000f, 1000f)
    cell.velocity = hugeVelocity

    cell.velocity.magnitude should be < hugeVelocity.magnitude
  }

  it should "move faster when it has a small mass" in {
    val small = new Cell(1)
    small.target = new Vector2(100f, 100f)
    val big = new Cell(2)
    big.target = new Vector2(100f, 100f)

    small.mass = Cell.MinMass
    big.mass = 100 * Cell.MinMass

    small.acceleration.magnitude should be > big.acceleration.magnitude
  }

  it should "return a state with the right info" in {
    val cell = new Cell(1)
    cell.mass = 100

    val state = cell.state

    state.id should equal(1)
    state.radius should equal(round(sqrt(cell.mass * Pi)))
  }

  it should "Be in the cell" in {
    val cell = new Cell(1)
    cell.mass = (pow(100f, 2) / Pi).toFloat

    val vec = new Vector2(42f, 42f)
    cell.contains(vec) should equal(true)
  }
  it should "Not be in the cell" in {
    val cell = new Cell(1)
    cell.mass = Cell.MinMass

    val vec = new Vector2(42f, 42f)
    cell.contains(vec) should equal(false)
  }

  it should "not move without setting its target" in {
    val cell = new Cell(1, new Vector2(42f, 42f))
    cell.behavior = new NoBehavior(cell)
    val grid = new Grid(200, 200)

    cell.update(1f, grid)

    cell.position should equal(new Vector2(42f, 42f))
  }

  it should "enforce a minimum mass" in {
    val cell = new Cell(1, new Vector2(0f, 0f))

    cell.mass = 0f

    cell.mass should equal(Cell.MinMass)
  }

  it should "lose mass per update" in {
    val cell = new Cell(1, new Vector2(0f, 0f))
    cell.mass = 1000f

    cell.update(1f, new Grid(0, 0))

    cell.mass should equal(1000f * (1f - Cell.MassDecayPerSecond))
  }

  it should "lose the same mass regardless of time increments" in {
    val cell1 = new Cell(1, new Vector2(0f, 0f))
    val cell2 = new Cell(2, new Vector2(0f, 0f))
    val grid = new Grid(0, 0)
    cell1.mass = 1000f
    cell2.mass = 1000f

    cell1.update(0.5f, grid)
    cell2.update(0.25f, grid)
    cell2.update(0.25f, grid)

    cell1.mass should equal(cell2.mass)
  }

  it should "update its behavior on update" in {
    val cell = new Cell(1, new Vector2(0f, 0f))
    cell.behavior = new TestBehavior

    cell.update(1f, new Grid(0, 0))

    cell.behavior shouldBe 'updated
  }

  it should "not go below 0 x" in {
    val cell = new Cell(1, new Vector2(-5, 5))
    val grid = new Grid(10, 10)

    cell.update(1f, grid)

    cell.position.x should be >= 0f
    cell.position.x should be <= 10f
    cell.position.y should be >= 0f
    cell.position.y should be <= 10f
  }

  it should "not go below 0 y" in {
    val cell = new Cell(1, new Vector2(10, -5))
    val grid = new Grid(10, 10);

    cell.update(1f, grid)

    cell.position.x should be >= 0f
    cell.position.x should be <= 10f
    cell.position.y should be >= 0f
    cell.position.y should be <= 10f
  }

  it should "not go outside grid x boundary" in {
    val cell = new Cell(1, new Vector2(12, 5))
    val grid = new Grid(10, 10);

    cell.update(1f, grid)

    cell.position.x should be >= 0f
    cell.position.x should be <= 10f
    cell.position.y should be >= 0f
    cell.position.y should be <= 10f
  }

  it should "not go outside grid y boundary" in {
    val cell = new Cell(1, new Vector2(5, 12))
    val grid = new Grid(10, 10);

    cell.update(1f, grid)

    cell.position.x should be >= 0f
    cell.position.x should be <= 10f
    cell.position.y should be >= 0f
    cell.position.y should be <= 10f
  }

  it should "not go outside two grid boundaries at the same time" in {
    val cell = new Cell(1, new Vector2(12, 12))
    val grid = new Grid(10, 10);

    cell.update(1f, grid)

    cell.position.x should be >= 0f
    cell.position.x should be <= 10f
    cell.position.y should be >= 0f
    cell.position.y should be <= 10f
  }

  it should "eat a cell smaller than 90% of its mass when contained in itself" in {
    val largeCell = new Cell(1, new Vector2(10, 10))
    val smallCell = new Cell(2, new Vector2(10, 10))
    val opponent = new Player(2, Vector2(10, 10))

    largeCell.mass = 30
    smallCell.mass = 27
    opponent.cells = List(smallCell)

    largeCell.eats(List(opponent))

    opponent.cells shouldBe empty
  }

  it should "not eat a cell between 90% to 100% of its mass when contained in itself" in {
    val largeCell = new Cell(1, new Vector2(10, 10))
    val smallCell = new Cell(2, new Vector2(10, 10))
    val opponent = new Player(2, Vector2(10, 10))

    largeCell.mass = Cell.MinMass + 1
    smallCell.mass = Cell.MinMass
    opponent.cells = List(smallCell)

    smallCell.eats(List(opponent))

    opponent.cells should contain only smallCell
  }

  it should "not eat a larger cell when into it" in {
    val largeCell = new Cell(1, new Vector2(10, 10))
    val smallCell = new Cell(2, new Vector2(10, 10))
    val opponent = new Player(2, Vector2(10, 10))

    largeCell.mass = Cell.MinMass + 1
    smallCell.mass = Cell.MinMass
    opponent.cells = List(largeCell)

    smallCell.eats(List(opponent))

    opponent.cells should contain only largeCell
  }

  "performAction" should "change target to match the one from the action" in {
    val cell = new Cell(1, new Vector2(12, 12))
    val grid = new Grid(100, 100);

    cell.performAction(Action(0, false, false, false, 0, Position(0f, 10f)))

    cell.target.state should equal(Position(0f, 10f))
  }
}
