package io.aigar.game

import com.github.jpbetz.subspace._
import java.util.Random

object Grid {
  final val WidthPerPlayer = 100
  final val HeightPerPlayer = 100
}
class Grid(val width: Int, val height: Int) {
  val random = new Random()

  def randomPosition = {
    val x = random.nextFloat() * width
    val y = random.nextFloat() * height
    new Vector2(x, y)
  }

  def state = {
    serializable.Dimensions(width, height)
  }
}
