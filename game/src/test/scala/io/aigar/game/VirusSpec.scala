package io.aigar.game

import com.github.jpbetz.subspace.Vector2
import io.aigar.game.serializable.Position
import org.scalatest.{FlatSpec, Matchers}


class VirusSpec extends FlatSpec with Matchers {
  "A Virus" should "create a state with the right info" in {
    val virus = new Virus(new Vector2(5, 6))

    virus.state should equal(new Position(5, 6))
  }
}
