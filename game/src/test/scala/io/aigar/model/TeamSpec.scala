package io.aigar.model

import org.scalatest._

class TeamSpec extends FlatSpec with Matchers {

  def withInMemDatabase(testCode: (TeamRepository, Team) => Any) {
    val teamRepository = new TeamRepository(None)
    val team = teamRepository.createTeam(Team(None, "EdgQWhJ!v&", "team1", 3))

    try{
      testCode(teamRepository, team)
    }
    finally {
      teamRepository.dropSchema
      teamRepository.closeConnection
    }
  }

  "The TeamDAO" should "update the score accordingly" in withInMemDatabase { (teamRepository, team) =>
    TeamDAO.addScore(teamRepository.db, team.id.get, 10)
    val updatedTeam = TeamDAO.findTeamById(teamRepository.db, team.id.get).get

    updatedTeam.score should equal(team.score + 10)
  }
}
