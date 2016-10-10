package io.aigar.model

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.Await

case class Team(id: Option[Int], teamSecret: String, teamName: String, score: Int)

class Teams(tag: Tag) extends Table[Team](tag, "TEAMS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def teamSecret = column[String]("TEAM_SECRET")
  def teamName = column[String]("TEAM_NAME")
  def score = column[Int]("SCORE", O.Default(0))
  def * = (id.?, teamSecret, teamName, score) <> (Team.tupled, Team.unapply)
}

object TeamDAO extends TableQuery(new Teams(_)) {
  lazy val teams = TableQuery[Teams]

  def create(db: Database, team: Team): Team = {
    Await.result(
      db.run(
        teams returning teams.map(_.id) into ((t, id) => t.copy(id = Some(id))) += team
      ), Duration.Inf
    )
  }

  def findById(db: Database, id: Int): Option[Team] = {
    Await.result(
      db.run(
        teams.filter(_.id === id)
          .result
        ).map(_.headOption
      ), Duration.Inf
    )
  }

  def update(db: Database, team: Team): Option[Team] ={
    Await.result(
      db.run(
        teams.filter(_.id === team.id)
          .update(team).map {
          case 0 => None
          case _ => Some(team)
        }
      ), Duration.Inf
    )
  }

  def deleteById(db: Database, id:Int): Boolean = {
    1 == Await.result(
          db.run(
            teams.filter(_.id === id)
              .delete
          ), Duration.Inf
        )
  }

  def getTeams(db: Database): List[Team] = {
    Await.result(
      db.run(
        teams.result
      ), Duration.Inf
    ).toList
  }

  def createSchema(db: Database): Unit = {
    Await.result(
      db.run(
        teams.schema.create
      ), Duration.Inf
    )
  }

  def dropSchema(db: Database): Unit = {
    Await.result(
      db.run(
        teams.schema.drop
      ), Duration.Inf
    )
  }
}



