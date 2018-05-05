package io.aigar.score

import com.typesafe.scalalogging.LazyLogging
import io.aigar.model.PlayerRepository
import java.util.concurrent.LinkedBlockingQueue

/**
  * ScoreThread is running all the time. The game thread sends score update
  * messages and this thread persists them to the DB.
  */

class ScoreThread(playerRepository: PlayerRepository) extends Runnable
    with LazyLogging {
  final val modificationQueue = new LinkedBlockingQueue[ScoreModification]
  var running: Boolean = true;

  def run: Unit = {
    while (running) {
      saveScore
    }
  }

  def addScoreModification(modification: ScoreModification): Unit = {
    modificationQueue.add(modification)
  }

  def saveScore: Unit = {
    val modification = modificationQueue.take

    logger.debug(s"Player ${modification.player_id} gained ${modification.value} points.")
    playerRepository.addScore(modification.player_id, modification.value)
  }
}
