package io.aigar.model

import org.scalatest._

class PlayerRepositorySpec extends FlatSpec
    with Matchers
    with io.aigar.test.TestWithDatabase {

  def withInMemDatabase(testCode: (List[PlayerModel]) => Any) {
    cleanDB()
    val player1 = playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&", "player1"))
    val player2 = playerRepository.createPlayer(PlayerModel(None, "not_that_secret", "player2"))
    val player3 = playerRepository.createPlayer(PlayerModel(None, "xx3ddfas3", "player3"))

    val listPlayers = List(player1, player2, player3)

    testCode(listPlayers)
  }

  it should "create a new player object and return it" in withInMemDatabase { (listPlayers) =>
    val player4 = playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&", "player4"))

    assert(player4.playerSecret === "EdgQWhJ!v&")
    assert(player4.playerName === "player4")
  }

  it should "not be possible to create two players with the same name" in withInMemDatabase { (listPlayers) =>
    an [org.h2.jdbc.JdbcSQLException] should be thrownBy playerRepository.createPlayer(
      PlayerModel(None, "EdgQWhJ!v&", "player1")
    )
  }

  it should "not be possible to create a player with an empty name" in withInMemDatabase { (listPlayers) =>
    an [IllegalArgumentException] should be thrownBy playerRepository.createPlayer(
      PlayerModel(None, "EdgQWhJ!v&", "")
    )
  }

  it should "not be possible to create a player with a whitespace name" in withInMemDatabase { (listPlayers) =>
    an [IllegalArgumentException] should be thrownBy playerRepository.createPlayer(
      PlayerModel(None, "EdgQWhJ!v&", "  ")
    )
  }

  it should "read the player 2 by its id and equal it" in withInMemDatabase { (listPlayers) =>
    assert(playerRepository.readPlayer(listPlayers(1).id.get).get === listPlayers(1))
  }

  it should "read a non-existing id and return nothing" in withInMemDatabase { (listPlayers) =>
    assert(playerRepository.readPlayer(258741).isEmpty)
  }

  it should "update an existing player with success" in withInMemDatabase { (listPlayers) =>
    val playerToUpdate = PlayerModel(playerRepository.readPlayer(listPlayers.head.id.get).get.id, "new_secret", "new player")
    val playerUpdated = playerRepository.updatePlayer(playerToUpdate).get

    assert(playerUpdated === playerToUpdate)
    assert(playerRepository.readPlayer(playerUpdated.id.get).get !== listPlayers.head)
  }

  it should "try to update a non-existing player without success" in withInMemDatabase { (listPlayers) =>
    assert(playerRepository.updatePlayer(PlayerModel(Some(258741), "", "")).isEmpty)
  }

  it should "delete an existing player with success" in withInMemDatabase { (listPlayers) =>
    assert(playerRepository.deletePlayer(listPlayers.head.id.get))
    assert(playerRepository.getPlayers().size === listPlayers.size - 1)
  }

  it should "try to delete a non-existing player without success" in withInMemDatabase { (listPlayers) =>
    assert(!playerRepository.deletePlayer(258741))
    assert(playerRepository.getPlayers().size === listPlayers.size)
  }

  it should "return a list with all the players" in withInMemDatabase { (listPlayers) =>
    assert(playerRepository.getPlayers() === listPlayers)
  }

  it should "be possible to create two repos using the same DB" in {
    new PlayerRepository(db)
    noException should be thrownBy new PlayerRepository(db)
  }

  it should "get a player by its secret" in withInMemDatabase { (listPlayers) =>
    assert(!playerRepository.readPlayerBySecret("EdgQWhJ!v&").isEmpty)
  }

  "getPlayersWithScores" should "return a player with joined scores" in withInMemDatabase { (players) =>
    val player0Id = players(0).id.get
    val player1Id = players(1).id.get
    scoreRepository.addScore(player0Id, 5f)
    scoreRepository.addScore(player0Id, 10f)
    scoreRepository.addScore(player1Id, 42f)

    val scores = playerRepository.getPlayersWithScores map {
      case (PlayerModel(Some(playerId), _, _), ScoreModel(_, _, value, _)) => (playerId, value)
    }

    scores should contain allOf (
      (player0Id, 5f),
      (player0Id, 10f),
      (player1Id, 42f)
    )
  }
}
