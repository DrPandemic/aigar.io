import javax.servlet.ServletContext

import com.typesafe.scalalogging.LazyLogging
import org.scalatra.{LifeCycle}

import io.aigar.controller.{AdminController, GameController, LeaderboardController}
import io.aigar.game.GameThread
import io.aigar.model.PlayerRepository
import io.aigar.score.ScoreThread

object ScalatraBootstrap {
  final val passwordLength = 28
}

class ScalatraBootstrap extends LifeCycle
                        with LazyLogging {
  logger.info("Bootstrapping application.")
  var playerRepository: PlayerRepository = null
  var game: GameThread = null
  var scoreThread: ScoreThread = null
  final val adminPassword = (new scala.util.Random(new java.security.SecureRandom())).alphanumeric.take(ScalatraBootstrap.passwordLength).mkString
  final val path = "/api/1"

  override def init(context: ServletContext): Unit = {
    appInit()

    logger.info("****************************")
    logger.info("***Administrator password***")
    logger.info(adminPassword)
    logger.info("****************************")

    context.mount(new AdminController(adminPassword, game, playerRepository), s"$path/admin/*")
    context.mount(new LeaderboardController(playerRepository), s"$path/leaderboard/*")
    context.mount(new GameController(game, playerRepository), s"$path/game/*")
  }

  /*
   * Separated method for testing purposes.
   */
  def appInit(players: Option[PlayerRepository] = None): Unit = {
    playerRepository = players.getOrElse(new PlayerRepository(None))
    scoreThread = new ScoreThread(playerRepository)
    game = new GameThread(scoreThread)

    launchThreads
  }

  private def closeDbConnection: Unit = {
    playerRepository.closeConnection
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
