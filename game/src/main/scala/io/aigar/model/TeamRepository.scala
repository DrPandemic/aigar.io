package io.aigar.model

import slick.driver.H2Driver.api._

class TeamRepository(db: Database) {
  createSchema()

  def createTeam(team: Team): Team = {
    TeamDAO.create(db, team)
  }

  def readTeam(id: Int): Option[Team] = {
    TeamDAO.findById(db, id)
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
    List(Team(None, "EdgQWhJ!v&", "New Team", 0), Team(None,"not_that_secret", "your_team", 50))
  }

  def createSchema(): Unit ={
    TeamDAO.createSchema(db)
  }

  def dropSchema(): Unit ={
    TeamDAO.dropSchema(db)
  }
}
