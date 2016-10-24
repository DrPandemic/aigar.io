package io.aigar.controller

import io.aigar.controller.response._
import io.aigar.model.TeamRepository

import org.scalatra.json._

class LeaderboardController(teamRepository: TeamRepository) extends AigarStack with JacksonJsonSupport {
  get("/") {
    LeaderboardResponse(
      List(
        LeaderboardEntry(465, "asd", 132),
        LeaderboardEntry(745, "wow", 2),
        LeaderboardEntry(7, "such score", 22)
      ))
  }
}
