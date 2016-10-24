package io.aigar.controller

import io.aigar.controller.response._
import io.aigar.model.TeamRepository

import org.scalatra.json._

class LeaderboardController(teamRepository: TeamRepository) extends AigarStack with JacksonJsonSupport {
  get("/") {
    val teams = teamRepository.getTeams
      .map(team => {
             LeaderboardEntry(team.id.get, team.teamName, team.score)
           })
    LeaderboardResponse(teams)
  }
}
