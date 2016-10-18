package io.aigar.game

import io.aigar.game.serializable.Position

object Resources {
  final val MaxRegular = 200
  final val MaxSilver = 100
  final val MaxGold = 50
}

class Resources {
  var regular = initRegular
  var silver = initSilver
  var gold = initGold

  def initRegular: List[Position] ={
    List.fill(Resources.MaxRegular)(Position(scala.util.Random.nextInt(900*3), scala.util.Random.nextInt(700*3)))
  }

  def initSilver: List[Position] ={
    List.fill(Resources.MaxSilver)(Position(scala.util.Random.nextInt(900*3), scala.util.Random.nextInt(700*3)))
  }

  def initGold: List[Position] ={
    List.fill(Resources.MaxGold)(Position(scala.util.Random.nextInt(900*3), scala.util.Random.nextInt(700*3)))
  }

  def state = {
    serializable.Resources(
      regular,
      silver,
      gold
    )
  }
}
