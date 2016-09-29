package io.aigar.controller.response

case class GameState(id: Int, tick: Int)
case class GameStateResponse(data: GameState)

case class GameCreation(id: Int, url: String)
case class GameCreationResponse(data: GameCreation)

case class SuccessResponse(data: String)
