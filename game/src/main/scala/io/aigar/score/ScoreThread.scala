package io.aigar.score

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue, TimeUnit}

/**
 * ScoreThread is running all the time. The game thread sends score update
 * messages and this thread persists them to the DB.
 */

class ScoreThread extends Runnable {
  final val messageQueue: BlockingQueue[ScoreMessage] = new LinkedBlockingQueue[ScoreMessage]
  var running: Boolean = true;

  def run: Unit = {
    while (!running) {
      val message = messageQueue.poll(1, TimeUnit.SECONDS)
      if(message != null) {
        println("Some magic here")
      }
    }
  }
}
