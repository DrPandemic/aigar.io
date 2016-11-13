package io.aigar.controller.response

trait AdminQuery {
  val administrator_password: String
}

case class SetRankedDurationQuery(
  val administrator_password: String,
  val duration: Int
) extends AdminQuery

/**
  * Class used to transfer commands from the AdminController to the game thread.
  */
trait AdminCommand

case class SetRankedDurationCommand(
  val duration: Int
) extends AdminCommand
