package io.aigar.controller

import io.aigar.controller.response._
import io.aigar.model.PlayerRepository

import org.scalatra.json._

class LeaderboardController(playerRepository: PlayerRepository) extends AigarStack with JacksonJsonSupport {
  get("/") {
    val players = playerRepository.getPlayers
      .map(player => {
             LeaderboardEntry(player.id.get, player.playerName, player.score)
           })
    LeaderboardResponse(players)
  }
}
