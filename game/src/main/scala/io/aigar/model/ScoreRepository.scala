package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._
import java.util.logging.{Level, Logger}

object ScoreRepository {
  final val MinimumNumberOfScore = 30
  final val MaximumNumberOfScore = 40
}

class ScoreRepository(db: Database) {
  def addScore(playerId: Int, value: Float): Unit = {
    ScoreDAO.addScore(db, playerId, value)
  }

  def getScoresForPlayer(playerId: Int): List[ScoreModel] = {
    ScoreDAO.getScoresForPlayer(db, playerId)
  }

  def createSchema: Unit = {
    ScoreDAO.createSchema(db)
  }

  def dropSchema: Unit = {
    ScoreDAO.dropSchema(db)
  }

  def compress: Unit = {
    ScoreDAO.compress(db)
  }
}
