package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._

class TeamRepository(inMemory: Boolean) {
  val cpds = new ComboPooledDataSource
  val db = createDatabase(inMemory)
  createSchema

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

  def createDatabase(inMemory: Boolean): Database ={
    if(inMemory){
      cpds.setDriverClass("org.h2.Driver")
      cpds.setJdbcUrl("jdbc:h2:mem:test")
      cpds.setUser("root")
      cpds.setPassword("")
      cpds.setMinPoolSize(1)
      cpds.setAcquireIncrement(1)
      cpds.setMaxPoolSize(50)
    }
    Database.forDataSource(cpds)
  }

  def createSchema: Unit ={
    TeamDAO.createSchema(db)
  }

  def dropSchema: Unit ={
    TeamDAO.dropSchema(db)
  }

  def closeConnection: Unit ={
    cpds.close()
  }
}
