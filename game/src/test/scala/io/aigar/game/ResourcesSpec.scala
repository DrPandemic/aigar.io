package io.aigar.game

import org.scalatest._

class ResourcesSpec extends FlatSpec with Matchers {
  "Resources" should "spawn at the right quantity" in {
    val resources = new Resources(new Grid(0, 0))

    val state = resources.state

    state.regular should have size Resources.MaxRegular
    state.silver should have size Resources.MaxSilver
    state.gold should have size Resources.MaxGold
  }

  it should "respawn resources when there's less than minimal quantity" in {
    val resources = new Resources(new Grid(0, 0))

    resources.listRegular = resources.listRegular.take(Resources.MinRegular)
    resources.listSilver = resources.listSilver.take(Resources.MinSilver)
    resources.listGold = resources.listGold.take(Resources.MinGold)

    resources.update

    resources.listRegular.length should be > Resources.MinRegular
    resources.listSilver.length should be > Resources.MinSilver
    resources.listGold.length should be > Resources.MinGold
  }

  it should "not respawn resources when the quantity is maximal" in {
    val resources = new Resources(new Grid(0, 0))

    resources.listRegular = resources.listRegular.take(Resources.MaxRegular)
    resources.listSilver = resources.listSilver.take(Resources.MaxSilver)
    resources.listGold = resources.listGold.take(Resources.MaxGold)

    resources.update

    resources.listRegular.length should equal(Resources.MaxRegular)
    resources.listSilver.length should equal(Resources.MaxSilver)
    resources.listGold.length should equal(Resources.MaxGold)
  }
}
