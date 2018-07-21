package io.aigar.model

import org.scalatest._

class ScoreRepositorySpec extends FlatSpec
    with Matchers
    with io.aigar.test.TestWithDatabase {

  def withInMemDatabase(testCode: (PlayerModel) => Any) {
    cleanDB()
    val player = playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&", "player1"))

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

  (5 to 50).map { i =>
    "compress" should s"reduce the number of entry from every player with $i" in withInMemDatabase { (player) =>
      val p1Id = player.id.get
      val p2Id = playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&2", "player2")).id.get
      val itt = ScoreRepository.MaximumNumberOfScore + i
      for (_ <- 1 to itt) {
        scoreRepository.addScore(p1Id, 11f)
        scoreRepository.addScore(p2Id, 7f)
      }

      scoreRepository.compress

      val scores1 = scoreRepository.getScoresForPlayer(p1Id)
      val scores2 = scoreRepository.getScoresForPlayer(p2Id)

      scores1.length should be <= ScoreRepository.MinimumNumberOfScore
      scores2.length should be <= ScoreRepository.MinimumNumberOfScore
      scores1.map { s => s.scoreModification }.sum should (be > 11f * itt - 1 and be < 11f * itt + 1)
      scores2.map { s => s.scoreModification }.sum should (be > 7f * itt - 1 and be < 7f * itt + 1)
    }
  }

  it should "not trigger when under the expected number of records" in withInMemDatabase { (player) =>
    val p1Id = player.id.get
    val p2Id = playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&2", "player2")).id.get
    val itt =  ScoreRepository.MaximumNumberOfScore - 1
    for (_ <- 1 to itt) {
      scoreRepository.addScore(p1Id, 11f)
      scoreRepository.addScore(p2Id, 7f)
    }

    scoreRepository.compress

    scoreRepository.getScoresForPlayer(p1Id)should have length itt
    scoreRepository.getScoresForPlayer(p2Id)should have length itt
  }
}
