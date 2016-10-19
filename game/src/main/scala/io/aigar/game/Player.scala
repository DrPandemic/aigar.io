package io.aigar.game

import scala.math.round
import com.github.jpbetz.subspace._

class Player(val id: Int, startPosition: Vector2) {
  var cells = List(new Cell(0, startPosition))
  var active = true;

  def update(deltaSeconds: Float, grid: Grid) {
    cells.foreach { _.update(deltaSeconds, grid) }
  }

  def state = {
    val mass = round(cells.map(_.mass).sum).toInt
    serializable.Player(id,
                        id.toString,
                        mass,
                        active,
                        cells.map(_.state).toList)
  }

  /**
   * Should be called whenever an external action occurs (e.g. we receive a
   * command coming from the AI of a team).
   */
  def onExternalAction = {
    cells.foreach { _.behavior.onPlayerActivity }
  }
}
