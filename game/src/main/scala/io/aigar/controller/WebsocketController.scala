package io.aigar.controller

import io.aigar.game._
import io.aigar.model.PlayerRepository
import io.aigar.controller.response._
import org.json4s.MappingException
import org.scalatra.json._
import scala.util.{Success, Try }

import org.scalatra.atmosphere._
import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.scalalogging.LazyLogging

class WebsocketController(game: GameThread, playerRepository: PlayerRepository)
  extends AigarStack with AtmosphereSupport with LazyLogging {
  atmosphere("/") {
    new AtmosphereClient {
      def receive = {
        case Connected => logger.info("New connection")
        case Disconnected(disconnector, Some(error)) =>
        case Error(Some(error)) =>
        case TextMessage(text) => send("ECHO: " + text)
        case JsonMessage(json) => broadcast(json)
      }
    }
  }
}
