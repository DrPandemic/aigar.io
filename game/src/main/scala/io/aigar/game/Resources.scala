package io.aigar.game

import io.aigar.game.serializable.Position

class Resources {
  var regular = initRegular
  var silver = initSilver
  var gold = initGold

  def initRegular: List[Position] ={
    List.fill(200)(Position(scala.util.Random.nextInt(900*3), scala.util.Random.nextInt(700*3)))
  }

  def initSilver: List[Position] ={
    List.fill(100)(Position(scala.util.Random.nextInt(900*3), scala.util.Random.nextInt(700*3)))
  }

  def initGold: List[Position] ={
    List.fill(50)(Position(scala.util.Random.nextInt(900*3), scala.util.Random.nextInt(700*3)))
  }

  def state = {
    serializable.Resources(
      regular,
      silver,
      gold
    )
  }
}
