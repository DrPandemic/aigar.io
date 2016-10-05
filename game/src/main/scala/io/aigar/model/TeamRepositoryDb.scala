package io.aigar.model
import slick.driver.H2Driver.api._
import slick.lifted.TableQuery

class TeamRepositoryDb(db: Database) extends TeamRepository {
  val teamsTable = TableQuery[Teams]
  initSchema()

  def createTeam(team: Team): Boolean = {
    true
    /*TeamDAO.create(db, team)*/
  }

  def readTeam(id: Int): Team = {
    /*TeamDAO.findById(db, id)*/
    Team(1, "", "", 2)
  }

  def updateTeam(id: Int): Boolean = {
    true
  }

  def deleteTeam(id: Int): Boolean = {
    /*TeamDAO.deleteById(db, id)*/
    true
  }

  def getTeams(): List[Team] = {
    List(Team(1,"very_secret", "my_team", 25), Team(2,"not_that_secret", "your_team", 50))
  }

  private def initSchema(): Unit ={
    db.run(teamsTable.schema.create)
  }
}
