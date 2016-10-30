package io.aigar.score

import io.aigar.model.TeamRepository
import java.util.concurrent.LinkedBlockingQueue

/**
 * ScoreThread is running all the time. The game thread sends score update
 * messages and this thread persists them to the DB.
 */

class ScoreThread(teamRepository: TeamRepository) extends Runnable {
  final val modificationQueue = new LinkedBlockingQueue[ScoreModification]
  var running: Boolean = true;

  def run: Unit = {
    while (running) {
      saveScore
    }
  }

  def saveScore: Unit = {
    val modification = modificationQueue.take

    teamRepository.addScore(modification.team_id, modification.value)
  }
}
