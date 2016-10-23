import io.aigar.game.GameThread
import io.aigar.controller._
import io.aigar.score.ScoreThread
import org.scalatra._
import javax.servlet.ServletContext

import io.aigar.model.TeamRepository

class ScalatraBootstrap extends LifeCycle {
  var teamRepository: TeamRepository = null
  var game: GameThread = null
  var scoreThread: ScoreThread

  override def init(context: ServletContext): Unit = {
    appInit()

    val path = "/api/1"
    context.mount(new LeaderboardController, s"$path/leaderboard/*")
    context.mount(new GameController(game, teamRepository), s"$path/game/*")
  }

  /*
   * Separated method for testing purposes.
   */
  def appInit(teams: Option[TeamRepository] = None): Unit = {
    teamRepository = teams.getOrElse(new TeamRepository(None))
    scoreThread = new ScoreThread
    game = new GameThread(scoreThread, fetchTeamIDs)

    launchThreads
  }

  private def closeDbConnection {
    teamRepository.closeConnection
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    game.running = false
    closeDbConnection

    scoreThread.running = false
  }

  def launchThreads {
    new Thread(scoreThread).start
    new Thread(game).start
  }

  def fetchTeamIDs: List[Int] = {
    val teams = teamRepository.getTeams()

    teams.map(_.id).flatten  // only keep IDs that are not None
  }
}
