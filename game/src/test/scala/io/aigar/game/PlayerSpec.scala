import io.aigar.game._
import org.scalatest._
import org.scalatest.LoneElement._
import com.github.jpbetz.subspace._

class PlayerSpec extends FlatSpec with Matchers {
  "A Player" should "start with a cell at its start position" in {
    val player = new Player(0, new Vector2(42f, 42f), new Grid(100, 100))

    player.cells.loneElement.position should equal(new Vector2(42f, 42f))
  }

  it should "move its cells on update" in {
    val player = new Player(0, new Vector2(0f, 0f), new Grid(100, 100))
    val target = new Vector2(100f, 100f)
    player.cells.head.target = target

    val initialDistance = player.cells.head.position.distanceTo(target)

    player.update(1f)

    val finalDistance = player.cells.head.position.distanceTo(target)

    initialDistance should be > finalDistance
  }

  it should "generate a state with the right info" in {
    val grid = new Grid(100, 100)
    val player = new Player(1, new Vector2(0f, 0f), grid)
    player.cells = List(new Cell(1, grid), new Cell(2, grid))
    player.cells(0).mass = 10
    player.cells(1).mass = 25

    val state = player.state

    state.total_mass should equal(35)
    state.id should equal(1)
    state.cells should have size 2
  }

   it should "execute behavior callbacks when calling the external action callback" in {
     val player = new Player(1, new Vector2(0f, 0f))
     player.cells = List(new Cell(1), new Cell(2))
     player.cells.foreach { _.behavior = new TestBehavior }

     player.onExternalAction

     val behaviors = player.cells.map(_.behavior.asInstanceOf[TestBehavior])
     all(behaviors) shouldBe 'active
   }
}
