package io.aigar.model
import slick.driver.H2Driver.api._

class TeamRepositoryDb(db:Database) extends TeamRepository {
  def addTeam(team:Team):Boolean = {
    true
  }

  def getTeam(id:Int):Team = {
    Team(1, "very_secret", "my_team", 25)
  }

  def updateTeam(id:Int):Boolean = {
    true
  }
  def removeTeam(id:Int):Boolean = {
    true
  }
  def getTeams():List[Team] = {
    List(Team(1,"very_secret", "my_team", 25), Team(2,"not_that_secret", "your_team", 50))
  }

  private def initSchema(): Unit ={
    db.run(TableQuery[Teams].schema.create)
  }
}
