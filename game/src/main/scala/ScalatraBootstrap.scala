import io.aigar.controller._
import org.scalatra._
import javax.servlet.ServletContext
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.slf4j.LoggerFactory
import slick.driver.H2Driver.api._

class ScalatraBootstrap extends LifeCycle {
  val logger = LoggerFactory.getLogger(getClass)
  val cpds = new ComboPooledDataSource

  logger.info("Created c3p0 connection pool")
  override def init(context: ServletContext): Unit = {
    val path = "/api/1"
    val db = Database.forDataSource(cpds)
    context.mount(new LeaderboardController(db), s"$path/leaderboard/*")
    context.mount(new GameController(db), s"$path/game/*")
  }

  private def closeDbConnection() {
    logger.info("Closing c3po connection pool")
    cpds.close
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection
  }
}
