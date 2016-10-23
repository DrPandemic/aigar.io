package io.aigar.controller

import io.aigar.game._
import io.aigar.model.TeamRepository
import io.aigar.controller.response._
import org.json4s.MappingException
import org.scalatra.json._
import scala.util.Try

class GameController(game: GameThread, teamRepository: TeamRepository)
  extends AigarStack with JacksonJsonSupport {

  get("/:id") {
    GameStateResponse(
      Try(params("id").toInt).toOption match {
        case Some(id) => game.gameState(id) match {
          case Some(state) => {
            fillPlayerName(state)
          }
          case None => halt(404)
        }
        case None => halt(400)
      }
    )
  }

  def fillPlayerName(state: serializable.gameState): serializable.gameState = {
    val teams = teamRepository.getTeams
    state.copy(players = state.players.map(
                 (player) => {
                   val name = teams.find(_.id.get == player.id) match {
                     case Some(team) => team.teamName
                     case None => halt(500)
                   }
                   player.copy(name = name)
                 }))
  }

  post("/") {
    GameCreationResponse(GameCreation(42, "http://somewherekindasafe.xyz"))
  }

  post("/:id/action") {
    try {
      val query = parse(request.body).extract[ActionQuery]
      teamRepository.readTeamBySecret(query.team_secret) match {
        case Some(team) => {
          val actions = ActionQueryWithId(params("id").toInt, team.id.get, query)
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
