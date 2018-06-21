import io.aigar.score._
import io.aigar.model.ScoreRepository

import org.scalatest._
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers.any

class ScoreThreadSpec extends FlatSpec with Matchers with MockitoSugar {
  "saveScore" should "take a modification and save it" in {
    val repo = mock[ScoreRepository]
    val score = new ScoreThread(repo)
    score.modificationQueue.add((ScoreModification(0, 42f), 2))

    score.saveScore

    verify(repo).addScore(0, 42f * 2)
  }

  it should "ignore 0" in {
    val repo = mock[ScoreRepository]
    val score = new ScoreThread(repo)
    score.modificationQueue.add((ScoreModification(0, 0f), 2))

    score.saveScore

    verify(repo, times(0)).addScore(any[Int], any[Float])
  }

  it should "squash values" in {
    val repo = mock[ScoreRepository]
    val score = new ScoreThread(repo)
    score.modificationQueue.add((ScoreModification(0, 10f), 2))
    score.modificationQueue.add((ScoreModification(0, 30f), 4))
    // score.modificationQueue.add((ScoreModification(1, 30f), 4))

    score.saveScore

    verify(repo).addScore(0, 140f)
    // verify(repo).addScore(1, 120f)
  }

  "addScoreModification" should "add a ScoreModification to the modificationQueue" in {
    val score = new ScoreThread(null)

    score.addScoreModification(ScoreModification(0, 42.2f), 1)

    score.modificationQueue should contain theSameElementsAs List((ScoreModification(0, 42.2f), 1))
  }
}
