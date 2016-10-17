package io.aigar.game

import io.aigar.game.serializable.Position
import org.scalatest._


class FoodSpec extends FlatSpec with Matchers{
  //Very dummy test right now. Will update it after I do the Resources spawn / respawn
  "Food" should "generate a state with the predefined regular, silver and gold resources in it" in {
    val food = new Food
    val state = food.state

    state.regular should equal (List(new Position(12, 12)))
    state.silver should equal (List(new Position(112, 112)))
    state.gold should equal (List(new Position(212, 212)))
  }
}
