package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._
import java.util.logging.{Level, Logger}
import io.aigar.score.ScoreMessage

class TeamRepository(databaseName: Option[String]) {
  val cpds = new ComboPooledDataSource
  val dev = sys.props.get("testing") match {
    case Some(value) => value == "true"
    case None => false
  }
  val db = createDatabase(dev)
  createSchema

  def createTeam(team: Team): Team = {
    TeamDAO.createTeam(db, team)
  }

  def readTeam(id: Int): Option[Team] = {
    TeamDAO.findTeamById(db, id)
  }

  def readTeamBySecret(teamSecret: String): Option[Team] = {
    TeamDAO.findTeamBySecret(db, teamSecret)
  }

  def addScore(team_id: Int, value: Int): Unit ={
    TeamDAO.addScore(db, team_id, value)
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

  def createDatabase(inMemory: Boolean): Database = {
    val dbName = databaseName match {
      case Some(value) => value
      case None => new scala.util.Random(new java.security.SecureRandom()).toString
    }

    if(inMemory) {
      Logger.getLogger("com.mchange.v2.c3p0").setLevel(Level.OFF)
      cpds.setDriverClass("org.h2.Driver")
      cpds.setJdbcUrl("jdbc:h2:mem:" + dbName)
      cpds.setUser("root")
      cpds.setPassword("")
      cpds.setMinPoolSize(1)
      cpds.setAcquireIncrement(1)
      cpds.setMaxPoolSize(50)
    }
    Database.forDataSource(cpds)
  }

  def createSchema: Unit = {
    TeamDAO.createSchema(db)
  }

  def dropSchema: Unit = {
    TeamDAO.dropSchema(db)
  }

  def closeConnection: Unit = {
    cpds.close()
  }
}
