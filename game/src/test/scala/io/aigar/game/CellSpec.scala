import io.aigar.game._
import org.scalatest._
import com.github.jpbetz.subspace._

class CellSpec extends FlatSpec with Matchers {
  "A Cell" should "not initiate movement when its target is on itself" in {
    val cell = new Cell(1, new Grid(100, 100), new Vector2(42f, 42f))
    cell.target = new Vector2(42f, 42f)

    cell.update(1f)

    cell.position should equal(new Vector2(42f, 42f))
  }

  it should "move towards its target when it is away from itself" in {
    val cell = new Cell(1, new Grid(100, 100), new Vector2(42f, 42f))
    cell.target = new Vector2(1000f, 1000f)

    val initialDistance = cell.position.distanceTo(cell.target)

    cell.update(1f)

    val finalDistance = cell.position.distanceTo(cell.target)

    initialDistance should be > finalDistance
  }

  it should "have a maximal velocity" in {
    val cell = new Cell(1, new Grid(100, 100))

    val hugeVelocity = new Vector2(1000f, 1000f)
    cell.velocity = hugeVelocity

    cell.velocity.magnitude should be < hugeVelocity.magnitude
  }

  it should "move faster when it has a small mass" in {
    val grid = new Grid(100, 100)
    val small = new Cell(1, grid)
    small.target = new Vector2(100f, 100f)
    val big = new Cell(2, grid)
    big.target = new Vector2(100f, 100f)

    small.mass = 1
    big.mass = 1000

    small.acceleration.magnitude should be > big.acceleration.magnitude
  }

  it should "return a state with the right info" in {
    val cell = new Cell(1, new Grid(100, 100))
    cell.mass = 100

    val state = cell.state

    state.id should equal(1)
    state.mass should equal(100)
  }

  it should "not move without setting its target" in {
    val cell = new Cell(1, new Vector2(42f, 42f))
    
    cell.update(1f)

    cell.position should equal(new Vector2(42f, 42f))
  }

  it should "enforce a minimum mass" in {
    var cell = new Cell(1, new Vector2(0f, 0f))

    cell.mass = 0f

    cell.mass should equal(Cell.MinMass)
  }

  it should "lose mass per update" in {
    val cell = new Cell(1, new Vector2(0f, 0f))
    cell.mass = 1000f

    cell.update(1f)

    cell.mass should equal(1000f * (1f - Cell.MassDecayPerSecond))
  }

  it should "lose the same mass regardless of time increments" in {
    val cell1 = new Cell(1, new Vector2(0f, 0f))
    val cell2 = new Cell(2, new Vector2(0f, 0f))
    cell1.mass = 1000f
    cell2.mass = 1000f

    cell1.update(0.5f)
    cell2.update(0.25f)
    cell2.update(0.25f)

    cell1.mass should equal(cell2.mass)
  }

  it should "update its behavior on update" in {
    val cell = new Cell(1, new Vector2(0f, 0f))
    cell.behavior = new TestBehavior

    cell.update(1f)

    cell.behavior shouldBe 'updated
  }

  it should "not go below 0 x" in {
    val cell = new Cell(1, new Grid(10, 10), new Vector2(-5, 5))

    cell.position.x should be >= 0f
    cell.position.x should be <= 10f
    cell.position.y should be >= 0f
    cell.position.y should be <= 10f
  }

  it should "not go below 0 y" in {
    val cell = new Cell(1,new Grid(10, 10), new Vector2(10, -5))

    cell.position.x should be >= 0f
    cell.position.x should be <= 10f
    cell.position.y should be >= 0f
    cell.position.y should be <= 10f
  }

  it should "not go outside grid x boundary" in {
    val cell = new Cell(1,new Grid(10, 10), new Vector2(12, 5))

    cell.position.x should be >= 0f
    cell.position.x should be <= 10f
    cell.position.y should be >= 0f
    cell.position.y should be <= 10f
  }

  it should "not go outside grid y boundary" in {
    val cell = new Cell(1, new Grid(10, 10), new Vector2(5, 12))

    cell.position.x should be >= 0f
    cell.position.x should be <= 10f
    cell.position.y should be >= 0f
    cell.position.y should be <= 10f
  }

  it should "not go outside two grid boundaries at the same time" in {
    val cell = new Cell(1, new Grid(10, 10), new Vector2(12, 12))

    cell.position.x should be >= 0f
    cell.position.x should be <= 10f
    cell.position.y should be >= 0f
    cell.position.y should be <= 10f
  }
}
