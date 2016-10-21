package io.aigar.game

import io.aigar.controller.response.{ActionQuery}

/**
  * Class used to transfer actions from the GameController to the game thread.
  */

case class ActionQueryWithId(game_id: Int, team_id: Int, query: ActionQuery)
