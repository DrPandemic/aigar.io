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
  seed: Boolean,
  playerCount: Int
)

case class CreatePlayerQuery(
  administrator_password: String,
  player_name: String
)
case class PlayerResult(
  player_secret: String,
  player_id: Int
)
case class CreatePlayerResponse(
  data: PlayerResult
)

case class RestartThreadQuery(
  administrator_password: String,
  running: Boolean
)

case class SetRankedMultiplierQuery(
  administrator_password: String,
  multiplier: Int
)

case class PauseQuery(
  administrator_password: String,
  paused: Boolean
)
case class DisableLeaderboardQuery(
  administrator_password: String,
  disabled: Boolean
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

case class SetRankedMultiplierCommand(
  multiplier: Int
) extends AdminCommand

case class PauseCommand(
  paused: Boolean
) extends AdminCommand

case class DisableLeaderboardCommand(
  disabled: Boolean
) extends AdminCommand
