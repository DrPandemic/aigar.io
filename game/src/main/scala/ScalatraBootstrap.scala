import io.aigar.game.GameThread
import io.aigar.controller._
import org.scalatra._
import javax.servlet.ServletContext

import io.aigar.model.TeamRepository

class ScalatraBootstrap extends LifeCycle {
  val teamRepository = new TeamRepository()
  val game = new GameThread

  override def init(context: ServletContext): Unit = {
    launchGameLoop

    val path = "/api/1"
    context.mount(new LeaderboardController, s"$path/leaderboard/*")
    context.mount(new GameController(game), s"$path/game/*")
  }

  private def closeDbConnection {
    teamRepository.dropSchema
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
