package io.aigar.websocket

import io.aigar.game.serializable
import java.util.concurrent.LinkedBlockingQueue
import com.typesafe.scalalogging.LazyLogging

/**
  *  The websocket thread runs all the time. It's used to send game state updates
  *  to websocket clients.
  */

class WebsocketThread() extends Runnable with LazyLogging {
  final val gameStateUpdateQueue = new LinkedBlockingQueue[serializable.GameState]
  var running: Boolean = true;

  def run: Unit = {
    while (running) {
      broadcastUpdate
    }
  }

  def addGameState(state: serializable.GameState): Unit = {
    gameStateUpdateQueue.add(state)
  }

  def broadcastUpdate: Unit = {
    val state = gameStateUpdateQueue.take
  }
}
