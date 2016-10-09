package io.aigar.model

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery

class TeamRepositoryMem(db: Database) extends TeamRepository {

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
    /*if (TeamDAO.deleteById(db, id) == id) true
    else false*/
    if(id == 1) true
    else false
  }

  def getTeams(): List[Team] = {
    List(Team(1, "EdgQWhJ!v&", "New Team", 0), Team(2,"not_that_secret", "your_team", 50))
  }
}
