package io.aigar.model

import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.{Await, Future}

case class PlayerModel(id: Option[Int], playerSecret: String, playerName: String, var score: Float)

class Players(tag: Tag) extends Table[PlayerModel](tag, "PLAYERS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def playerSecret = column[String]("PLAYER_SECRET")
  def playerName = column[String]("PLAYER_NAME")
  def score = column[Float]("SCORE", O.Default(0f))
  def * = (id.?, playerSecret, playerName, score) <> (PlayerModel.tupled, PlayerModel.unapply)
}

object PlayerDAO extends TableQuery(new Players(_)) {
  lazy val players = TableQuery[Players]

  /**
   * This function uses the TableQuery[Players] to access all the players and find the one
   * we just added (with no id) and returns it from the database with the auto-generated id.
   */
  def createPlayer(db: Database, player: PlayerModel): PlayerModel = {
    Await.result(
      db.run(
        players returning players.map(_.id) into ((p, id) => p.copy(id = Some(id))) += player
      ), Duration.Inf
    )
  }

  def findPlayerById(db: Database, id: Int): Option[PlayerModel] = {
    Await.result(
      db.run(
        players.filter(_.id === id)
          .result
        ).map(_.headOption
      ), Duration.Inf
    )
  }

  def findPlayerBySecret(db: Database, playerSecret: String): Option[PlayerModel] = {
    Await.result(
      db.run(
        players.filter(_.playerSecret === playerSecret)
          .result
      ).map(_.headOption
      ), Duration.Inf
    )
  }

  def addScore(db: Database, player_id: Int, value: Float): Unit = {
    val sql = sqlu"""update PLAYERS
                     set score = score + ${value}
                     where id = ${player_id}"""
    // TODO Log errors if there's any
    Await.result(
      db.run(
        sql
      )
      , Duration.Inf
    )
  }

  def updatePlayer(db: Database, player: PlayerModel): Option[PlayerModel] = {
    Await.result(
      db.run(
        players.filter(_.id === player.id)
          .update(player).map {
          case 0 => None
          case _ => Some(player)
        }
      ), Duration.Inf
    )
  }

  def deletePlayerById(db: Database, id:Int): Boolean = {
    1 == Await.result(
          db.run(
            players.filter(_.id === id)
              .delete
          ), Duration.Inf
        )
  }

  def getPlayers(db: Database): List[PlayerModel] = {
    Await.result(
      db.run(
        players.result
      ), Duration.Inf
    ).toList
  }

  def createSchema(db: Database): Unit = {
    def createTableIfNotInTables(tables: Vector[MTable]): Future[Unit] = {
      if (!tables.exists(_.name.name == players.baseTableRow.tableName)) {
        db.run(players.schema.create)
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
        players.schema.drop
      ), Duration.Inf
    )
  }
}



