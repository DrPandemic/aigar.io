package io.aigar.score

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

/**
 * ScoreThread is running all the time. The game thread sends score update
 * messages and this thread persiste them to the DB.
 */

class ScoreThread extends Runnable {
  final val messageQueue: BlockingQueue[ScoreMessage] = new LinkedBlockingQueue[ScoreMessage]()

  def run: Unit = {
    while (true) {
      val message = messageQueue.take()
    }
  }
}
