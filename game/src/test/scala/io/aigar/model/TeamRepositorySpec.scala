package io.aigar.model

import org.scalatra.test.specs2._

class TeamRepositorySpec extends MutableScalatraSpec {
  val teamRepositoryMem = new TeamRepositoryMem
  val team1 = Team(1, "EdgQWhJ!v&", "New Team", 0)
  val team2 = Team(2,"not_that_secret", "your_team", 50)

  teamRepositoryMem.createTeam(team1) should_=== team1

  teamRepositoryMem.readTeam(1) should_=== team1
  teamRepositoryMem.readTeam(3) should_=== null

  teamRepositoryMem.updateTeam(1) should_=== true
  teamRepositoryMem.updateTeam(3) should_=== false

  teamRepositoryMem.deleteTeam(1) should_=== true
  teamRepositoryMem.deleteTeam(3) should_=== false

  teamRepositoryMem.getTeams() should_=== List(team1, team2)
}
