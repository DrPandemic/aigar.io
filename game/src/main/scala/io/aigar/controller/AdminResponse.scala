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
  player_name: String
)
case class PlayerSecret(
  player_secret: String
)
case class CreatePlayerResponse(
  data: PlayerSecret
)

case class RestartThreadQuery(
  administrator_password: String,
  running: Boolean
)

/**
  * Class used to transfer commands from the AdminController to the game thread.
  */
trait AdminCommand

case class SetRankedDurationCommand(
  duration: Int
) extends AdminCommand

case class RestartThreadCommand(
  playerIDs: List[Int]
) extends AdminCommand
