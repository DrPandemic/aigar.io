package io.aigar.model

import org.scalatest._

class PlayerRepositorySpec extends FlatSpec with Matchers {

  def withInMemDatabase(testCode: (PlayerRepository, List[PlayerModel]) => Any) {
    val playerRepository = new PlayerRepository(None)
    val player1 = playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&", "player1", 0))
    val player2 = playerRepository.createPlayer(PlayerModel(None, "not_that_secret", "player2", 50))
    val player3 = playerRepository.createPlayer(PlayerModel(None, "xx3ddfas3", "player3", 56))

    val listPlayers = List(player1, player2, player3)

    try{
      testCode(playerRepository, listPlayers)
    }
    finally {
      playerRepository.dropSchema
      playerRepository.closeConnection
    }
  }

  it should "create a new player object and return it" in withInMemDatabase { (playerRepository, listPlayers) =>
    val player4 = playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&", "player4", 0))

    assert(player4.playerSecret === "EdgQWhJ!v&")
    assert(player4.playerName === "player4")
    assert(player4.score === 0)
  }

  it should "read the player 2 by its id and equal it" in withInMemDatabase { (playerRepository, listPlayers) =>
    assert(playerRepository.readPlayer(listPlayers(1).id.get).get === listPlayers(1))
  }

  it should "read a non-existing id and return nothing" in withInMemDatabase { (playerRepository, listPlayers) =>
    assert(playerRepository.readPlayer(258741).isEmpty)
  }

  it should "update an existing player with success" in withInMemDatabase { (playerRepository, listPlayers) =>
    val playerToUpdate = PlayerModel(playerRepository.readPlayer(listPlayers.head.id.get).get.id, "new_secret", "new player", 500)
    val playerUpdated = playerRepository.updatePlayer(playerToUpdate).get

    assert(playerUpdated === playerToUpdate)
    assert(playerRepository.readPlayer(playerUpdated.id.get).get !== listPlayers.head)
  }

  it should "try to update a non-existing player without success" in withInMemDatabase { (playerRepository, listPlayers) =>
    assert(playerRepository.updatePlayer(PlayerModel(Some(258741), "", "", 500)).isEmpty)
  }

  it should "delete an existing player with success" in withInMemDatabase { (playerRepository, listPlayers) =>
    assert(playerRepository.deletePlayer(listPlayers.head.id.get))
    assert(playerRepository.getPlayers().size === listPlayers.size - 1)
  }

  it should "try to delete a non-existing player without success" in withInMemDatabase { (playerRepository, listPlayers) =>
    assert(!playerRepository.deletePlayer(258741))
    assert(playerRepository.getPlayers().size === listPlayers.size)
  }

  it should "return a list with all the players" in withInMemDatabase { (playerRepository, listPlayers) =>
    assert(playerRepository.getPlayers() === listPlayers)
  }

  it should "be possible to create two repos using the same DB" in {
    new PlayerRepository(Some("something"))
    noException should be thrownBy new PlayerRepository(Some("something"))
  }

  it should "get a player by its secret" in withInMemDatabase { (playerRepository, listPlayers) =>
    assert(!playerRepository.readPlayerBySecret("EdgQWhJ!v&").isEmpty)
  }
}
