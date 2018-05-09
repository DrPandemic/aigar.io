package io.aigar.controller.response

import io.aigar.controller.response.AdminCommand
import io.aigar.game.serializable.{GameState, Position}

import org.scalatra.atmosphere._

case class ErrorResponse(error: String)

case class GameStateResponse(data: GameState)

case class GameCreationQuery(player_secret: String)
case class GameCreation(id: Int)
case class GameCreationResponse(data: GameCreation)
case class GameCreationCommand(
  gameId: Int
) extends AdminCommand

case class SuccessResponse(data: String)

case class LeaderboardEntry(player_id: Int, name: String, score: Float)
case class LeaderboardResponse(data: List[LeaderboardEntry])

case class Action(
  cell_id: Int,
  burst: Boolean,
  split: Boolean,
  trade: Int,
  target: Position
)
case class ActionQuery(player_secret: String, actions: List[Action])

// Websocket
case class GameStateMessage(content: Int) extends ProtocolMessage[Int]
