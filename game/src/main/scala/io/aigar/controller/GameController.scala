package io.aigar.controller

import io.aigar.game._
import io.aigar.model.PlayerRepository
import io.aigar.controller.response._
import org.json4s.MappingException
import org.scalatra.json._
import scala.util.{Success, Try }

class GameController(game: GameThread, playerRepository: PlayerRepository)
  extends AigarStack with JacksonJsonSupport {

  get("/:id") {
    GameStateResponse(
      Try(params("id").toInt).toOption match {
        case Some(id) => game.gameState(id) match {
          case Some(state) => fillPlayerName(state)
          case None => halt(404)
        }
        case None => halt(400)
      }
    )
  }

  def fillPlayerName(state: serializable.GameState): serializable.GameState = {
    val players = playerRepository.getPlayers
    state.copy(players = state.players.map(
                 (player) => {
                   val name = player.id match {
                     case 0 => "Player"
                     case 1 => "Bot 1"
                     case 2 => "Bot 2"
                     case 3 => "Bot 3"
                     case 4 => "Bot 4"
                   }
                   // val name = players.find(_.id.get == player.id) match {
                   //   case Some(player) => player.playerName
                   //   case None if player.id < 0 => "Bot " + math.abs(player.id)
                   //   case None => "Player"
                   //   case None => halt(500)
                   // }
                   player.copy(name = name)
                 }))
  }

  post("/") {
    Try(parse(request.body).extract[GameCreationQuery]) match {
      case Success(query: GameCreationQuery) => {
        playerRepository.readPlayerBySecret(query.player_secret) match {
          case Some(player) => {
            val command = GameCreationCommand(player.id.get)
            game.adminCommandQueue.put(command)
            GameCreationResponse(GameCreation(player.id.get))
          }
          case None => halt(403)
        }
      }
      case _ => halt(422)
    }
  }

  post("/:id/action") {
    try {
      val query = parse(request.body).extract[ActionQuery]
      playerRepository.readPlayerBySecret(query.player_secret) match {
        case Some(player) => {
          val actions = ActionQueryWithId(params("id").toInt, player.id.get, query.actions)
          game.actionQueue.put(actions)
        }
        case None => halt(403)
      }
    } catch {
      case e: MappingException => halt(422)
      case e: java.lang.NumberFormatException => halt(400)
    }

    SuccessResponse("ok")
  }
}
