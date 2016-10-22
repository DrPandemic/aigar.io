package io.aigar.game

import org.scalatest._

class ResourcesSpec extends FlatSpec with Matchers {
  "Resources" should "spawn at the right quantity" in {
    val game = new Game(1, 15)
    val resources = new Resources(game.grid)

    val state = resources.state

    state.regular should have size Resources.MaxRegular
    state.silver should have size Resources.MaxSilver
    state.gold should have size Resources.MaxGold
  }
}
