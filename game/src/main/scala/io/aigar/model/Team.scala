package io.aigar.model

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

case class Team(id: Int, teamSecret: String, teamName: String, score: Int)

class Teams(tag: Tag) extends Table[Team](tag, "TEAMS") {
  def id = column[Int]("ID", O.PrimaryKey)
  def teamSecret = column[String]("TEAM_SECRET")
  def teamName = column[String]("TEAM_NAME")
  def score = column[Int]("SCORE", O.Default(0))
  def * = (id, teamSecret, teamName, score) <> (Team.tupled, Team.unapply)
}

object TeamDAO extends TableQuery(new Teams(_)) {
  lazy val teams = TableQuery[Teams]
  def findById(db: Database, id: Int): Future[Option[Team]] = {
    db.run(this.filter(_.id === id).result).map(_.headOption)
  }

  def create(db: Database, team: Team): Future[Team] = {
    db.run(this returning this.map(_.id) into ((t, id) => t.copy(id = id)) += team)
  }

  def deleteById(db: Database, id:Int): Future[Int] = {
    db.run(this.filter(_.id === id).delete)
  }

  def getTeams(db: Database): Future[Seq[Teams#TableElementType]] = {
    db.run(this.result)
  }

  def initSchema(db: Database):Future[Unit] = {
    db.run(this.schema.create)
  }
}



