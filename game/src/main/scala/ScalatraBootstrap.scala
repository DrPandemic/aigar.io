import io.aigar.game.GameThread
import io.aigar.controller._
import io.aigar.score.ScoreThread
import org.scalatra._
import javax.servlet.ServletContext

import io.aigar.model.TeamRepository

class ScalatraBootstrap extends LifeCycle {
  val teamRepository = ScalatraBootstrap.fixedTeamRepository.getOrElse(new TeamRepository(None))
  val scoreThread = new ScoreThread
  val game = new GameThread(scoreThread, fetchTeamIDs)

  override def init(context: ServletContext): Unit = {
    launchThreads

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

    scoreThread.running = false
  }

  def launchThreads {
    new Thread(scoreThread).start
    new Thread(game).start
  }

  def fetchTeamIDs: List[Int] = {
    val teams = teamRepository.getTeams()
    
    // TODO remove this once we have seeding
    // No teams in the DB? Provide two fake teams to have something to look at
    if (teams.isEmpty) {
      return List(1,2)
    }

    teams.map(_.id).flatten  // only keep IDs that are not None
  }
}
object ScalatraBootstrap {
  // USED BY ScalatraBootstrapSpec TEST ONLY
  //
  // Scalatra finds "ScalatraBootstrap" by itself on init.
  // However, it fails to find it if we add constructor parameters to our ScalatraBootstrap.
  // Using class variables like this allows us to pass a custom team repo (instead of creating one)
  // while testing the constructor.
  var fixedTeamRepository: Option[TeamRepository] = None
}
