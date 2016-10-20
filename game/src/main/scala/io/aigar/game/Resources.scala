package io.aigar.game

import com.github.jpbetz.subspace.Vector2

object Resources {
  final val MaxRegular = 200
  final val MaxSilver = 100
  final val MaxGold = 50
}

class Resources(grid: Grid) {
  var regular = initRegular
  var silver = initSilver
  var gold = initGold

  def initRegular: List[Vector2] ={
    List.fill(Resources.MaxRegular)(grid.randomPosition)
  }

  def initSilver: List[Vector2] ={
    List.fill(Resources.MaxSilver)(grid.randomPosition)
  }

  def initGold: List[Vector2] ={
    List.fill(Resources.MaxGold)(grid.randomPosition)
  }

  def state = {
    serializable.Resources(
      regular,
      silver,
      gold
    )
  }
}
