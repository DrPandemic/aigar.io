package io.aigar.model

import org.scalatest._

class ScoreRepositorySpec extends FlatSpec
    with Matchers
    with io.aigar.test.TestWithDatabase {

  def withInMemDatabase(testCode: (PlayerModel) => Any) {
    cleanDB()
    val player = playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&", "player1", 0))

    testCode(player)
  }

  "getScoresForPlayer" should "returns an empty list when possible" in withInMemDatabase { (player) =>
    scoreRepository.getScoresForPlayer(player.id.get) shouldBe empty
  }

  it should "returns a list of saved scores" in withInMemDatabase { (player) =>
    val playerId = player.id.get
    scoreRepository.addScore(playerId, 10f)
    scoreRepository.addScore(playerId, 5f)
    scoreRepository.addScore(playerId, -42f)

    val scores = scoreRepository.getScoresForPlayer(playerId) map {
      case ScoreModel(_, pId, score, _) => (pId, score)
    }

    scores should contain allOf (
      (playerId, 10f),
      (playerId, 5f),
      (playerId, -42f)
    )
  }

  "addScore" should "create entries with increasing timestamp" in withInMemDatabase { (player) =>
    val playerId = player.id.get
    scoreRepository.addScore(playerId, 10f)
    scoreRepository.addScore(playerId, 5f)

    val scores = scoreRepository.getScoresForPlayer(playerId) map {
      case ScoreModel(_, _, _, timestamp) => timestamp
    }

    assert(scores(0).get.before(scores(1).get))
  }
}
