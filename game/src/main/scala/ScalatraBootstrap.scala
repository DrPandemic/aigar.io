import io.aigar.game.GameThread
import io.aigar.controller._
import io.aigar.score.ScoreThread
import org.scalatra._
import javax.servlet.ServletContext

import io.aigar.model.TeamRepository

object ScalatraBootstrap {
  // Scalatra fails to init if we add a ctor param to our ScalatraBootstrap.
  // This allows us to pass a team repo while testing (instead of creating one).
  var fixedTeamRepository: Option[TeamRepository] = None
}
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
