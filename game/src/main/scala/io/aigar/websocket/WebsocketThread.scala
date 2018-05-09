package io.aigar.websocket

import io.aigar.game.serializable
import io.aigar.controller.response._

import java.util.concurrent.LinkedBlockingQueue
import com.typesafe.scalalogging.LazyLogging
import org.scalatra.atmosphere._
import org.scalatra.json._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  *  The websocket thread runs all the time. It's used to send game state updates
  *  to websocket clients.
  */

class WebsocketThread() extends Runnable with LazyLogging {
  var running: Boolean = true;

  private final val gameStateUpdateQueue = new LinkedBlockingQueue[serializable.GameState]
  private final val networkRefresh = 3
  private var lastBroadcast = 0L
  private implicit lazy val jsonFormats: Formats = DefaultFormats
  private val serialization = org.json4s.jackson.Serialization

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
    val currentTime = System.currentTimeMillis()

    if ((currentTime - lastBroadcast) > (1000 / networkRefresh)) {
      AtmosphereClient.broadcastAll(TextMessage(serialization.write(state)))
      lastBroadcast = currentTime
    }
  }
}
