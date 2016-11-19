package io.aigar.controller.response

case class AdminQuery(
  administrator_password: String
)

case class SetRankedDurationQuery(
  administrator_password: String,
  duration: Int
)

case class SeedPlayersQuery(
  administrator_password: String,
  seed: Boolean
)

case class CreatePlayerQuery(
  administrator_password: String,
  player_name: Boolean
)
case class PlayerSecretResponse(
  player_secret: String
)
case class CreatePlayerResponse(
  data: PlayerSecretResponse
)

/**
  * Class used to transfer commands from the AdminController to the game thread.
  */
trait AdminCommand

case class SetRankedDurationCommand(
  val duration: Int
) extends AdminCommand
