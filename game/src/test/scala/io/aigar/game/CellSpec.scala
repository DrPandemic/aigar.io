import io.aigar.game._
import org.scalatest._
import com.github.jpbetz.subspace._

class CellSpec extends FlatSpec with Matchers {
  "A Cell" should "not initiate movement when its target is on itself" in {
    val cell = new Cell(1, new Vector2(42f, 42f))
    cell.target = new Vector2(42f, 42f)

    cell.update(1f)

    cell.position should equal(new Vector2(42f, 42f))
  }
  
  it should "move towards its target when it is away from itself" in {
    val cell = new Cell(1, new Vector2(42f, 42f))
    cell.target = new Vector2(1000f, 1000f)

    val initialDistance = cell.position.distanceTo(cell.target)

    cell.update(1f)

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

    small.mass = 1
    big.mass = 1000

    small.acceleration.magnitude should be > big.acceleration.magnitude
  }

  it should "return a state with the right info" in {
    val cell = new Cell(1)
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
}

