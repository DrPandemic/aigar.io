import io.aigar.game.GameThread
import io.aigar.controller._
import org.scalatra._
import javax.servlet.ServletContext

import io.aigar.model.TeamRepository

class ScalatraBootstrap extends LifeCycle {
  val teamRepository = new TeamRepository(None)
  val game = new GameThread

  override def init(context: ServletContext): Unit = {
    launchGameLoop

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

  def launchGameLoop {
    new Thread(game).start
  }
}
