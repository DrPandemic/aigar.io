package io.aigar.game

import io.aigar.controller.response.Action

/**
  * Class used to transfer actions from the GameController to the game thread.
  */

case class ActionQueryWithId(game_id: Int, player_id: Int, actions: List[Action])
