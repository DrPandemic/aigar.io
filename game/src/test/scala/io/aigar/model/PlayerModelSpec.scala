package io.aigar.model

import org.scalatest.{FlatSpec, Matchers}

class PlayerModelSpec extends FlatSpec
    with Matchers
    with io.aigar.test.TestWithDatabase {

  def withInMemDatabase(testCode: (PlayerModel) => Any): Unit = {
    cleanDB()
    val player = playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&", "player1", 3))

    testCode(player)
  }

  "The PlayerDAO" should "update the score accordingly" in withInMemDatabase { (player) =>
    PlayerDAO.addScore(db, player.id.get, 10f)
    val updatedPlayer = PlayerDAO.findPlayerById(db, player.id.get).get

    updatedPlayer.score should equal(player.score + 10f)
  }
}
