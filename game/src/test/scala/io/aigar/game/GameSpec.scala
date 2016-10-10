import io.aigar.game._
import org.scalatest._

class GameSpec extends FlatSpec with Matchers {
  "A Game" should "generate a new state object every time (thread-safety)" in {
    val game = new Game(42)
    val state1 = game.state
    game.state should not be theSameInstanceAs(state1)
  }
}
