import io.aigar.servlet._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext): Unit = {
    val path = "/api/1"
    context.mount(new LeaderboardController(), s"$path/leaderboard/*")
  }
}
