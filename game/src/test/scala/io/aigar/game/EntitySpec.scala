import com.github.jpbetz.subspace.Vector2
import io.aigar.game.Entity
import org.scalatest.{FlatSpec, Matchers}

class EntitySpec extends FlatSpec with Matchers {
  class FakeEntity(rad: Float, pos: Vector2 = Vector2(0f, 0f)) extends Entity {
    def radius: Float = rad
    def position: Vector2 = pos
    def position_=(v: Vector2): Unit = {}
    override val _scoreModification: Float = 0f
  }

  "An Entity" should "contain a position inside its radius" in {
    val entity = new FakeEntity(100f)

    val vec = Vector2(50f, 0f)

    entity.contains(vec) should equal(true)
  }

  it should "not contain a position outside of its radius" in {
    val entity = new FakeEntity(1f)

    val vec = Vector2(50f, 0f)

    entity.contains(vec) should not be(true)
  }

  it should "overlap a fully contained entity" in {
    val container = new FakeEntity(100f)
    val contained = new FakeEntity(1f)

    container.overlaps(contained) should be(true)
  }

  it should "not overlap an entity that does not collide" in {
    val current = new FakeEntity(100f)
    val far = new FakeEntity(1f, Vector2(300f, 300f))

    current.overlaps(far) should not be(true)
  }

  it should "not overlap when it barely collides with another entity's center" in {
    val current = new FakeEntity(100f)
    val other = new FakeEntity(100f, Vector2(100f, 0f))

    current.overlaps(other) should not be(true)
  }

  it should "overlap when almost fully covers another entity" in {
    val current = new FakeEntity(100f)
    val other = new FakeEntity(55f, Vector2(50f, 0f))

    current.overlaps(other) should be(true)
  }
}
