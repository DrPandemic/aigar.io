package io.aigar.controller

import io.aigar.game._
import io.aigar.controller.response._
import io.aigar.model.PlayerRepository

import org.scalatra.json._

class LeaderboardController(gameThread: GameThread, playerRepository: PlayerRepository)
  extends AigarStack with JacksonJsonSupport {
  get("/") {
    val disabled = gameThread.gameState(Game.RankedGameId) match {
      case None => false
      case Some(state) => state.disabledLeaderboard
    }

    if (disabled) {
      LeaderboardResponse(List(), true)
    } else {
      val players = playerRepository.getPlayersWithScores
        .map{ case(player, score) => {
               LeaderboardEntry(player.id.get, player.playerName, score.scoreModification, score.timestamp.get)
             }}

      LeaderboardResponse(players, false)
    }

  }
}
