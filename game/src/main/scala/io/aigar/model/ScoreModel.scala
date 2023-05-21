package io.aigar.model

import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.{Await, Future}
import java.sql.Timestamp

case class ScoreModel(id: Option[Int], playerId: Int, scoreModification: Float, timestamp: Option[Timestamp])

class Scores(tag: Tag) extends Table[ScoreModel](tag, "SCORES") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def playerId = column[Int]("PLAYER_ID")
  def scoreModification = column[Float]("SCORE_MODIFICATION", O.Default(0f))
  def timestamp = column[Timestamp]("TIMESTAMP", O.SqlType("TIMESTAMP AS COALESCE(TIMESTAMP, CURRENT_TIMESTAMP)"))

  def * = (id.?, playerId, scoreModification, timestamp.?) <>  (ScoreModel.tupled, ScoreModel.unapply)
}

object ScoreDAO extends TableQuery(new Scores(_)) {
  import scala.math.Ordered.orderingToOrdered
  implicit val orderedModel: Ordering[ScoreModel] = Ordering.by(_.timestamp.get.getTime())
  lazy val scores = TableQuery[Scores]

  def addScore(db: Database, playerId: Int, value: Float): Unit = {
    Await.result(db.run(addScoreQuery(playerId, value)), Duration.Inf)
  }

  private def addScoreQuery(playerId: Int, value: Float) = {
    scores returning scores.map(_.id) into ((s, id) => s.copy(id = Some(id))) += ScoreModel(None, playerId, value, None)
  }

  private def addScoresQuery(scoreList: Seq[ScoreModel]) = {
    scores returning scores.map(_.id) into ((s, id) => s.copy(id = Some(id))) ++= scoreList
  }

  private def compressScores(scoreList: Seq[ScoreModel]): Seq[ScoreModel] = {
    val nbPlayers = scoreList.map { score: ScoreModel => score.playerId }.distinct.length
    if (scoreList.isEmpty || scoreList.length < ScoreRepository.MaximumNumberOfScore * nbPlayers) {
      return scoreList
    }

    val min = scoreList.min.timestamp.get.getTime()
    val max = scoreList.max.timestamp.get.getTime()
    val window = (max - min) / ScoreRepository.MinimumNumberOfScore + 1

    scoreList.groupBy(s => (s.playerId, (max - s.timestamp.get.getTime()) / window))
      .map { case ((id, _), values) =>
        ScoreModel(None, id, values.map(_.scoreModification).sum, values.max.timestamp)
      }.toSeq
  }

  def compress(db: Database): Unit = {
    val action = (for {
      results <- scores.result
      _ <- scores.delete
      _ <- addScoresQuery(this.compressScores(results))
    } yield ()).transactionally

    Await.result(db.run(action), Duration.Inf)
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
