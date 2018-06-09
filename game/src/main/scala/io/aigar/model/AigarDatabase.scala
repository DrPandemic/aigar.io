package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._
import java.util.logging.{Level, Logger}

object AigarDatabase {
  var cpds = new ComboPooledDataSource

  def getRandomName(): String = {
    new scala.util.Random(new java.security.SecureRandom()).toString
  }

  def createDatabase(databaseName: String, inMemory: Boolean): Database = {
    closeConnection()
    var cpds = new ComboPooledDataSource
    if(inMemory) {
      Logger.getLogger("com.mchange.v2.c3p0").setLevel(Level.OFF)
      cpds.setDriverClass("org.h2.Driver")
      cpds.setJdbcUrl("jdbc:h2:mem:" + databaseName)
      cpds.setUser("root")
      cpds.setPassword("")
      cpds.setMinPoolSize(1)
      cpds.setAcquireIncrement(1)
      cpds.setMaxPoolSize(50)
    }
    Database.forDataSource(cpds)
  }

  def closeConnection(): Unit = {
    cpds.close()
  }
}
