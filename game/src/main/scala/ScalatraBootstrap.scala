import javax.servlet.ServletContext

import com.typesafe.scalalogging.LazyLogging
import org.scalatra.{LifeCycle}
import slick.driver.H2Driver.api.Database

import io.aigar.controller.{AdminController, GameController, LeaderboardController}
import io.aigar.game.GameThread
import io.aigar.model._
import io.aigar.score.ScoreThread

object ScalatraBootstrap {
  final val PasswordLength = 28
}

class ScalatraBootstrap extends LifeCycle
                        with LazyLogging {
  logger.info("Bootstrapping application.")
  var playerRepository: PlayerRepository = null
  var scoreRepository: ScoreRepository = null
  var game: GameThread = null
  var scoreThread: ScoreThread = null
  final val adminPassword = (new scala.util.Random(new java.security.SecureRandom())).alphanumeric.take(ScalatraBootstrap.PasswordLength).mkString
  final val version = "/1"
  final val path = s"/api$version"

  override def init(context: ServletContext): Unit = {
    val database = AigarDatabase.createDatabase(AigarDatabase.getRandomName, false)
    appInit(database)

    logger.info("****************************")
    logger.info("***Administrator password***")
    logger.info(adminPassword)
    logger.info("****************************")

    context.mount(new AdminController(adminPassword, game, playerRepository, scoreRepository), s"$path/admin/*")
    context.mount(new LeaderboardController(playerRepository), s"$path/leaderboard/*")
    context.mount(new GameController(game, playerRepository), s"$path/game/*")
  }

  /*
   * Separated method for testing purposes.
   */
  def appInit(database: Database): Unit = {
    playerRepository = new PlayerRepository(database)
    scoreRepository = new ScoreRepository(database)
    scoreThread = new ScoreThread(scoreRepository)
    game = new GameThread(scoreThread)

    launchThreads
  }

  private def closeDbConnection: Unit = {
    AigarDatabase.closeConnection
  }

  override def destroy(context: ServletContext): Unit = {
    super.destroy(context)
    closeDbConnection

    scoreThread.running = false
    game.running = false
  }

  def launchThreads: Unit = {
    new Thread(scoreThread).start
    new Thread(game).start
  }
}
