import io.aigar.game._
import org.scalatest._

class GridSpec extends FlatSpec with Matchers {
  "A Grid" should "generate a state with the right width/height" in {
    val grid = new Grid(100, 200)

    val state = grid.state

    state.width should equal(100)
    state.height should equal(200)
  }
}
