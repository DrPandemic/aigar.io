import io.aigar.score._
import io.aigar.model.TeamRepository
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class ScoreThreadSpec extends FlatSpec with Matchers with MockitoSugar {
  "saveScore" should "take a modification and save it" in {
    val repo = mock[TeamRepository]
    val score = new ScoreThread(repo)
    score.modificationQueue.add(ScoreModification(0, 42))

    score.saveScore

    verify(repo).addScore(0, 42)
  }

  "addScoreModification" should "add a ScoreModification to the modificationQueue" in {
    val score = new ScoreThread(null)

    score.addScoreModification(ScoreModification(0, 42))

    score.modificationQueue should contain only ScoreModification(0, 42)
  }
}
