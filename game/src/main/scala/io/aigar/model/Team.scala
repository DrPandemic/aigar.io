package io.aigar.model

import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.{Await, Future}

case class Team(id: Option[Int], teamSecret: String, teamName: String, var score: Int)

class Teams(tag: Tag) extends Table[Team](tag, "TEAMS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def teamSecret = column[String]("TEAM_SECRET")
  def teamName = column[String]("TEAM_NAME")
  def score = column[Int]("SCORE", O.Default(0))
  def * = (id.?, teamSecret, teamName, score) <> (Team.tupled, Team.unapply)
}

object TeamDAO extends TableQuery(new Teams(_)) {
  lazy val teams = TableQuery[Teams]

  /**
   * This function uses the TableQuery[Teams] to access all the teams and find the one
   * we just added (with no id) and returns it from the database with the auto-generated id.
   */
  def createTeam(db: Database, team: Team): Team = {
    Await.result(
      db.run(
        teams returning teams.map(_.id) into ((t, id) => t.copy(id = Some(id))) += team
      ), Duration.Inf
    )
  }

  def findTeamById(db: Database, id: Int): Option[Team] = {
    Await.result(
      db.run(
        teams.filter(_.id === id)
          .result
        ).map(_.headOption
      ), Duration.Inf
    )
  }

  def findTeamBySecret(db: Database, teamSecret: String): Option[Team] = {
    Await.result(
      db.run(
        teams.filter(_.teamSecret === teamSecret)
          .result
      ).map(_.headOption
      ), Duration.Inf
    )
  }

  def updateScore(db: Database, team_id: Int, value: Int): Unit ={
    val sql = sqlu"""update TEAMS
                     set score = score + ${value}
                     where id = ${team_id}"""
    // TODO Log errors if there's any
    Await.result(
      db.run(
        sql
      )
      , Duration.Inf
    )
  }

  def updateTeam(db: Database, team: Team): Option[Team] ={
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

  def deleteTeamById(db: Database, id:Int): Boolean = {
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
    def createTableIfNotInTables(tables: Vector[MTable]): Future[Unit] = {
      if (!tables.exists(_.name.name == teams.baseTableRow.tableName)) {
        db.run(teams.schema.create)
      } else {
        Future()
      }
    }

    val createTableIfNotExist: Future[Unit] = db.run(MTable.getTables).flatMap(createTableIfNotInTables)

    Await.result(createTableIfNotExist, Duration.Inf)
  }

  def dropSchema(db: Database): Unit = {
    Await.result(
      db.run(
        teams.schema.drop
      ), Duration.Inf
    )
  }
}



