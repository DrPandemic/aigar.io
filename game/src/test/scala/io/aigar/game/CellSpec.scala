import io.aigar.game._
import org.scalatest._
import com.github.jpbetz.subspace._

class CellSpec extends FlatSpec with Matchers {
  "A Cell" should "not move when its target is on itself" in {
    val cell = new Cell()
    cell.position = new Vector2(42f, 42f)
    cell.target = new Vector2(42f, 42f)
    cell.update

    cell.position should equal(new Vector2(42, 42))
  }
}

