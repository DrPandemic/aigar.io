package io.aigar.game

import com.github.jpbetz.subspace.Vector2

object Resources {
  final val MaxRegular = 250
  final val MinRegular = 100
  final val MaxSilver = 100
  final val MinSilver = 50
  final val MaxGold = 50
  final val MinGold = 25
}

class Resources(grid: Grid) {
  var listRegular = initRegular
  var listSilver = initSilver
  var listGold = initGold

  def initRegular: List[Vector2] = {
    List.fill(Resources.MaxRegular)(grid.randomPosition)
  }

  def initSilver: List[Vector2] = {
    List.fill(Resources.MaxSilver)(grid.randomPosition)
  }

  def initGold: List[Vector2] = {
    List.fill(Resources.MaxGold)(grid.randomPosition)
  }

  def update: Unit = {
    listRegular = updateResources(listRegular, Resources.MinRegular, Resources.MaxRegular)
    listSilver = updateResources(listSilver, Resources.MinSilver, Resources.MaxSilver)
    listGold = updateResources(listGold, Resources.MinGold, Resources.MaxGold)
  }

  def updateResources(list: List[Vector2], min: Int, max: Int) = {
    val ratio = (list.length - min).toFloat / (max - min)
    if (scala.util.Random.nextFloat >= ratio) list ++ List(grid.randomPosition) else list
  }

  def state = {
    serializable.Resources(
      listRegular,
      listSilver,
      listGold
    )
  }
}
