package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._
import java.util.logging.{Level, Logger}

class ScoreRepository(db: Database) {
  def createSchema: Unit = {
    ScoreDAO.createSchema(db)
  }

  def dropSchema: Unit = {
    ScoreDAO.dropSchema(db)
  }
}
