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

  def updateTeam(team: Team): Option[Team] = {
    TeamDAO.update(db, team)
  }

  def deleteTeam(id: Int): Boolean = {
    TeamDAO.deleteById(db, id)
  }

  def getTeams(): List[Team] = {
    TeamDAO.getTeams(db)
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
