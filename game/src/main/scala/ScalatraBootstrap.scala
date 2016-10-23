import io.aigar.game.GameThread
import io.aigar.controller._
import io.aigar.score.ScoreThread
import org.scalatra._
import javax.servlet.ServletContext

import io.aigar.model.TeamRepository

class ScalatraBootstrap extends LifeCycle {
  val teamRepository = new TeamRepository(None)
  val scoreThread = new ScoreThread
  val game = new GameThread(scoreThread)

  override def init(context: ServletContext): Unit = {
    launchThreads

    val path = "/api/1"
    context.mount(new LeaderboardController, s"$path/leaderboard/*")
    context.mount(new GameController(game, teamRepository), s"$path/game/*")
  }

  private def closeDbConnection {
    teamRepository.closeConnection
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection
  }

  def launchThreads {
    new Thread(scoreThread).start
    new Thread(game).start
  }
}
