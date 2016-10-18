package io.aigar.game

import org.scalatest._

class ResourcesSpec extends FlatSpec with Matchers {
  "Resources" should "spawn at the right quantity" in {
    val resources = new Resources

    val state = resources.state

    state.regular.size should equal(Resources.MaxRegular)
    state.silver.size should equal(Resources.MaxSilver)
    state.gold.size should equal(Resources.MaxGold)
  }
}
