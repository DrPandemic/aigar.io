import io.aigar.game.GameThread
import io.aigar.controller._
import io.aigar.score.ScoreThread
import org.scalatra._
import javax.servlet.ServletContext

import io.aigar.model.PlayerRepository

class ScalatraBootstrap extends LifeCycle {
  var playerRepository: PlayerRepository = null
  var game: GameThread = null
  var scoreThread: ScoreThread = null
  final val adminPassword = (new scala.util.Random(new java.security.SecureRandom())).alphanumeric.take(28).mkString
  final val path = "/api/1"

  override def init(context: ServletContext): Unit = {
    appInit()

    println("****************************")
    println("***Administrator password***")
    println(adminPassword)
    println("****************************")

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
    game = new GameThread(scoreThread, fetchPlayerIDs)

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

  def fetchPlayerIDs: List[Int] = {
    val players = playerRepository.getPlayers()

    players.map(_.id).flatten  // only keep IDs that are not None
  }
}
