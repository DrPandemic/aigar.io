package io.aigar.game

import com.github.jpbetz.subspace._
import java.util.Random

object Grid {
  final val WidthPerPlayer = 100
  final val HeightPerPlayer = 100
}

class Grid(val width: Int, val height: Int) {
  val random = new Random()

  def randomPosition: Vector2 = {
    val x = random.nextFloat() * width
    val y = random.nextFloat() * height
    Vector2(x, y)
  }
  
  // Logic coming from http://stackoverflow.com/a/5838055/395386
  def randomRadiusPosition: Vector2 = {
    val angle = 2 * Math.PI * random.nextFloat()
    val a = random.nextFloat() + random.nextFloat()
    val radius = if(a > 1) 2-a else a

    val x = radius * Math.cos(angle).toFloat * (width/2) + width/2
    val y = radius * Math.sin(angle).toFloat * (height/2) + height/2
    Vector2(x, y)
  }

  def state = {
    serializable.Dimensions(width, height)
  }
}
