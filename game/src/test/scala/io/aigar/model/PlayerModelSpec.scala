package io.aigar.model

import org.scalatest.{FlatSpec, Matchers}

class PlayerModelSpec extends FlatSpec with Matchers {
  def withInMemDatabase(testCode: (PlayerRepository, PlayerModel) => Any): Unit = {
    val playerRepository = new PlayerRepository(None)
    val player = playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&", "player1", 3))

    try {
      testCode(playerRepository, player)
    }
    finally {
      playerRepository.dropSchema
      playerRepository.closeConnection
    }
  }

  "The PlayerDAO" should "update the score accordingly" in withInMemDatabase { (playerRepository, player) =>
    PlayerDAO.addScore(playerRepository.db, player.id.get, 10f)
    val updatedPlayer = PlayerDAO.findPlayerById(playerRepository.db, player.id.get).get

    updatedPlayer.score should equal(player.score + 10f)
  }
}
