package io.aigar.test

import io.aigar.model._

trait TestWithDatabase {
  var db = AigarDatabase.createDatabase(AigarDatabase.getRandomName, true)
  var playerRepository = new PlayerRepository(db)
  var scoreRepository = new ScoreRepository(db)

  def initDB(): Unit = {
    db = AigarDatabase.createDatabase(AigarDatabase.getRandomName, true)
    playerRepository = new PlayerRepository(db)
    scoreRepository = new ScoreRepository(db)
  }

  def cleanDB(): Unit = {
    scoreRepository.dropSchema
    scoreRepository.createSchema
    playerRepository.dropSchema
    playerRepository.createSchema
  }
}
