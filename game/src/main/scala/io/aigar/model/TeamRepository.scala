package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._

class TeamRepository() {
  val cpds = new ComboPooledDataSource
  val db = createDatabase(sys.props.get("testing").get == "true")
  createSchema

  def createTeam(team: Team): Team = {
    TeamDAO.createTeam(db, team)
  }

  def readTeam(id: Int): Option[Team] = {
    TeamDAO.findTeamById(db, id)
  }

  def updateTeam(team: Team): Option[Team] = {
    TeamDAO.updateTeam(db, team)
  }

  def deleteTeam(id: Int): Boolean = {
    TeamDAO.deleteTeamById(db, id)
  }

  def getTeams(): List[Team] = {
    TeamDAO.getTeams(db)
  }

  def createDatabase(inMemory: Boolean): Database ={
    if(inMemory){
      cpds.setDriverClass("org.h2.Driver")
      cpds.setJdbcUrl("jdbc:h2:mem:test"+ new scala.util.Random(new java.security.SecureRandom()))
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
