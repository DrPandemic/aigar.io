package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._
import java.util.logging.{Level, Logger}

class PlayerRepository(db: Database) {
  def createPlayer(player: PlayerModel): PlayerModel = {
    PlayerDAO.createPlayer(db, player)
  }

  def readPlayer(id: Int): Option[PlayerModel] = {
    PlayerDAO.findPlayerById(db, id)
  }

  def readPlayerBySecret(playerSecret: String): Option[PlayerModel] = {
    PlayerDAO.findPlayerBySecret(db, playerSecret)
  }

  def addScore(playerId: Int, value: Float): Unit ={
    PlayerDAO.addScore(db, playerId, value)
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

  def createSchema: Unit = {
    PlayerDAO.createSchema(db)
  }

  def dropSchema: Unit = {
    PlayerDAO.dropSchema(db)
  }
}
