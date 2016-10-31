package io.aigar.score

import io.aigar.model.PlayerRepository
import java.util.concurrent.LinkedBlockingQueue

/**
 * ScoreThread is running all the time. The game thread sends score update
 * messages and this thread persists them to the DB.
 */

class ScoreThread(playerRepository: PlayerRepository) extends Runnable {
  final val modificationQueue = new LinkedBlockingQueue[ScoreModification]
  var running: Boolean = true;

  def run: Unit = {
    while (running) {
      saveScore
    }
  }

  def saveScore: Unit = {
    val modification = modificationQueue.take

    playerRepository.addScore(modification.player_id, modification.value)
  }
}
