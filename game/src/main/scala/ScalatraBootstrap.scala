import io.aigar.game.GameThread
import io.aigar.controller._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext): Unit = {
    launchGameLoop

    val path = "/api/1"
    context.mount(new LeaderboardController(), s"$path/leaderboard/*")
    context.mount(new GameController(), s"$path/game/*")
  }

  def launchGameLoop {
    new Thread(new GameThread).start
  }
}
