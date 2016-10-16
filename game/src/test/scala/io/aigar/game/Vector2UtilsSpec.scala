import io.aigar.game.Vector2Utils._
import org.scalatest._
import com.github.jpbetz.subspace._

class Vector2UtilsSpec extends FlatSpec with Matchers {
  "A Vector2" should "generate a state with the right coordinates" in {
    val vector = new Vector2(100f, 200f)

    val state = vector.state

    state.x should equal(100f)
    state.y should equal(200f)
  }
}
