package io.aigar.game

import com.github.jpbetz.subspace._

class Player(val id: Int, startPosition: Vector2) {
  var cells = List(new Cell(0, startPosition))

  def update(deltaSeconds: Float) {
    cells.foreach { _.update(deltaSeconds) }
  }

  def state = {
    val mass = cells.map(_.mass).sum
    serializable.Player(id,
                        id.toString,
                        mass,
                        cells.map(_.state).toList)
  }
}
