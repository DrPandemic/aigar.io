package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._
import java.util.logging.{Level, Logger}

class PlayerRepository(databaseName: Option[String]) {
  val cpds = new ComboPooledDataSource
  val dev = sys.props.get("testing") match {
    case Some(value) => value == "true"
    case None => false
  }
  val db = createDatabase(dev)
  createSchema

  def createPlayer(player: PlayerModel): PlayerModel = {
    PlayerDAO.createPlayer(db, player)
  }

  def readPlayer(id: Int): Option[PlayerModel] = {
    PlayerDAO.findPlayerById(db, id)
  }

  def readPlayerBySecret(playerSecret: String): Option[PlayerModel] = {
    PlayerDAO.findPlayerBySecret(db, playerSecret)
  }

  def addScore(player_id: Int, value: Float): Unit ={
    PlayerDAO.addScore(db, player_id, value)
  }

  def updatePlayer(player: PlayerModel): Option[PlayerModel] = {
    PlayerDAO.updatePlayer(db, player)
  }

  def deletePlayer(id: Int): Boolean = {
    PlayerDAO.deletePlayerById(db, id)
  }

  def getPlayers(): List[PlayerModel] = {
    PlayerDAO.getPlayers(db)
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
    PlayerDAO.createSchema(db)
  }

  def dropSchema: Unit = {
    PlayerDAO.dropSchema(db)
  }

  def closeConnection: Unit = {
    cpds.close()
  }
}
