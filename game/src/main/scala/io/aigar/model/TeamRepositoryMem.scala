package io.aigar.model

class TeamRepositoryMem() extends TeamRepository {

  def createTeam(team: Team): Team = {
    Team(1, "EdgQWhJ!v&", "New Team", 0)
  }

  def readTeam(id: Int): Team = {
    if(id == 1) Team(1, "EdgQWhJ!v&", "New Team", 0)
    else null
  }

  def updateTeam(id: Int): Boolean = {
    if(id == 1) true
    else false
  }

  def deleteTeam(id: Int): Boolean = {
    if(id == 1) true
    else false
  }

  def getTeams(): List[Team] = {
    List(Team(1, "EdgQWhJ!v&", "New Team", 0), Team(2,"not_that_secret", "your_team", 50))
  }
}
