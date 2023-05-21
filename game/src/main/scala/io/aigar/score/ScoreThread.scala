package io.aigar.score

import com.typesafe.scalalogging.LazyLogging
import io.aigar.model.ScoreRepository
import scala.collection.JavaConversions._
import scala.collection.immutable._

import java.util.concurrent.LinkedBlockingQueue

/**
 * ScoreThread is running all the time. The game thread sends score update
 * messages and this thread persists them to the DB.
 */

class ScoreThread(scoreRepository: ScoreRepository) extends Runnable
                                                      with LazyLogging {
  final val modificationQueue = new LinkedBlockingQueue[(ScoreModification, Int)]
  var running: Boolean = true;

  def run: Unit = {
    while (running) {
      try {
        saveScore
        scoreRepository.compress
        Thread.sleep(2000)
      } catch {
        case e: org.h2.jdbc.JdbcSQLException => logger.debug(s"A SQL statement failed: ${e}.")
      }
    }
  }

  def addScoreModification(modification: ScoreModification, multiplier: Int): Unit = {
    modificationQueue.add((modification, multiplier))
  }

  def saveScore: Unit = {
    val scores = new java.util.ArrayList[(ScoreModification, Int)]()
    modificationQueue.drainTo(scores)

    scores.map {
      case (ScoreModification(player_id, value), multiplier) => (player_id, value * multiplier)
    }.groupBy(_._1).foreach { case (player_id, value) =>
        val score = value.foldLeft(0f) { case (acc, (_, score)) => acc + score }
        if (score > 0.001f || score < -0.001f) {
          logger.debug(s"Player ${player_id} gained ${score} points.")
          scoreRepository.addScore(player_id, score)
        }
    }
  }
}
