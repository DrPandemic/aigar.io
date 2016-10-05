import io.aigar.game.GameThread
import io.aigar.controller._
import org.scalatra._
import javax.servlet.ServletContext
import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._

class ScalatraBootstrap extends LifeCycle {
  val cpds = new ComboPooledDataSource
  val game = new GameThread

  override def init(context: ServletContext): Unit = {
    launchGameLoop

    val path = "/api/1"
    val db = Database.forDataSource(cpds)
    context.mount(new LeaderboardController, s"$path/leaderboard/*")
    context.mount(new GameController, s"$path/game/*")
  }

  private def closeDbConnection() {
    cpds.close
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection
  }

  def launchGameLoop {
    new Thread(game).start
  }
}
