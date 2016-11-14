package io.aigar.controller.response

case class AdminQuery(
  administrator_password: String
 )

case class SetRankedDurationQuery(
  administrator_password: String,
  duration: Int
)

/**
  * Class used to transfer commands from the AdminController to the game thread.
  */
trait AdminCommand

case class SetRankedDurationCommand(
  val duration: Int
) extends AdminCommand
