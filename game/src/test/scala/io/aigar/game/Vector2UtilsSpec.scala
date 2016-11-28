import io.aigar.game.Vector2Utils
import io.aigar.game.Vector2Utils._
import io.aigar.game.Position2Utils._
import io.aigar.game.serializable.Position
import org.scalatest._
import com.github.jpbetz.subspace._

class Vector2UtilsSpec extends FlatSpec with Matchers {
  "A Vector2" should "generate a state with the right coordinates" in {
    val vector = new Vector2(100f, 200f)

    val state = vector.state

    state.x should equal(100f)
    state.y should equal(200f)
  }

  it should "truncate a long vector" in {
    val vector = new Vector2(100f, 0f)

    val truncated = vector.truncate(5f)

    truncated.x should equal(5f)
    truncated.y should equal(0f)
  }

  it should "not truncate a short vector" in {
    val vector = new Vector2(1f, 1f)

    val truncated = vector.truncate(5f) 

    truncated should be theSameInstanceAs(vector)
  }

  it should "not crash when calling safeNormalize on a zero vector" in {
    val vector = new Vector2(0f, 0f)

    vector.safeNormalize should equal(vector)
  }

  it should "return a normalized vector for non-zero vectors" in {
    val vector = new Vector2(100f, 98f)

    vector.safeNormalize should equal(vector.normalize)
  }

  it should "return a random unit vector with a length of 1" in {
    val vector = Vector2Utils.randomUnitVector

    vector.magnitude should equal(1f)
  }

  "Position" should "generate a vector with the right coordinates" in {
    val position = new Position(100f, 200f)

    val vector = position.toVector

    vector.x should equal(100f)
    vector.y should equal(200f)
  }
}

