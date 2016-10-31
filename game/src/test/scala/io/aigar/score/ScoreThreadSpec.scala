import io.aigar.score._
import io.aigar.model.PlayerRepository
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class ScoreThreadSpec extends FlatSpec with Matchers with MockitoSugar {
  "saveScore" should "take a modification and save it" in {
    val repo = mock[PlayerRepository]
    val score = new ScoreThread(repo)
    score.modificationQueue.add(ScoreModification(0, 42))

    score.saveScore

    verify(repo).addScore(0, 42)
  }
}
