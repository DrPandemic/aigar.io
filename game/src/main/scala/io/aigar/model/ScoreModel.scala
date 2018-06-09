package io.aigar.model

import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.{Await, Future}
import java.sql.Timestamp
import java.util.Date

case class ScoreModel(id: Option[Int], playerId: Int, scoreModification: Float, timestamp: Timestamp)

class Scores(tag: Tag) extends Table[ScoreModel](tag, "SCORES") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def playerId = column[Int]("PLAYER_ID")
  def scoreModification = column[Float]("SCORE_MODIFICATION", O.Default(0f))
  def timestamp = column[Timestamp]("TIMESTAMP", O.SqlType("TIMESTAMP AS CURRENT_TIMESTAMP"))

  def * = (id.?, playerId, scoreModification, timestamp) <> (ScoreModel.tupled, ScoreModel.unapply)
}

object ScoreDAO extends TableQuery(new Scores(_)) {
  lazy val scores = TableQuery[Scores]

  def addScore(db: Database, playerId: Int, value: Float): Unit = {
    Await.result(
      db.run(
        (scores returning scores.map(_.id) into ((s, id) => s.copy(id = Some(id)))) +=
          ScoreModel(None, playerId, value, new Timestamp((new Date).getTime()))
      ), Duration.Inf
    )
  }

  def getScoresForPlayer(db: Database, playerId: Int): List[ScoreModel] = {
    Await.result(
      db.run(
        scores.filter(_.playerId === playerId).result
      ), Duration.Inf
    ).toList
  }

  def createSchema(db: Database): Unit = {
    def createTableIfNotInTables(tables: Vector[MTable]): Future[Unit] = {
      if (!tables.exists(_.name.name == scores.baseTableRow.tableName)) {
        db.run(scores.schema.create)
      } else {
        Future()
      }
    }

    val createTableIfNotExist: Future[Unit] = db.run(MTable.getTables).flatMap(createTableIfNotInTables)

    Await.result(createTableIfNotExist, Duration.Inf)
  }

  def dropSchema(db: Database): Unit = {
    def deleteTableIfNotInTables(tables: Vector[MTable]): Future[Unit] = {
      if (tables.exists(_.name.name == scores.baseTableRow.tableName)) {
        db.run(scores.schema.drop)
      } else {
        Future()
      }
    }

    val deleteTableIfNotExist: Future[Unit] = db.run(MTable.getTables).flatMap(deleteTableIfNotInTables)

    Await.result(deleteTableIfNotExist, Duration.Inf)
  }
}
