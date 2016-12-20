package io.aigar.game

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.math.round
import com.github.jpbetz.subspace.Vector2
import com.typesafe.scalalogging.LazyLogging
import io.aigar.score.ScoreModification
import io.aigar.controller.response.Action

/**
 * Game holds the logic for an individual game being played
 * (e.g. the ranked game or a private test game).
 */
object Game {
  final val RankedGameId = -1
  final val DefaultDuration = 60 * 20
  final val PrivateGameDuration = 60 * 10
  final val PrivateGameBotQuantity = 5
  final val MinimumNumberOfPlayerModificator = 10

  final val NanoSecondsPerMillisecond = 1000000f
  final val MillisecondsPerSecond = 1000f
  final val NanoSecondsPerSecond = NanoSecondsPerMillisecond * MillisecondsPerSecond

  final val TicksPerSecond = 15
  final val MillisecondsPerTick = round(MillisecondsPerSecond / TicksPerSecond)

  /**
    * Current time, in seconds.
    */
  def time: Float = {
    System.nanoTime / NanoSecondsPerSecond
  }
}

class Game(val id: Int,
           playerIds: List[Int],
           val duration: Int = Game.DefaultDuration)
    extends LazyLogging {
  logger.info(s"Launching game with ID $id.")

  val grid = new Grid(math.max(Game.MinimumNumberOfPlayerModificator, playerIds.length) * Grid.WidthPerPlayer,
                      math.max(Game.MinimumNumberOfPlayerModificator, playerIds.length) * Grid.HeightPerPlayer)
  val players = createPlayers
  val viruses = new Viruses(grid, math.max(Game.MinimumNumberOfPlayerModificator, playerIds.length))
  val resources = new Resources(grid)

  val startTime = Game.time
  var previousTime = 0f
  var currentTime = Game.MillisecondsPerTick / Game.MillisecondsPerSecond // avoid having an initial 0 delta time
  var tick = 0

  def update: Future[(List[ScoreModification], serializable.GameState)] = {
    Future {
      this.synchronized {
        val deltaSeconds = currentTime - previousTime
        var modifications = players.flatten {  _.update(deltaSeconds, grid, players) }
        modifications :::= viruses.update(grid, players)
        modifications :::= resources.update(grid, players)
        tick += 1

        previousTime = currentTime
        currentTime = Game.time
        (modifications, state)
      }
    }
  }

  def performAction(player_id: Int, actions: List[Action]): Future[List[ScoreModification]] = {
    Future {
      this.synchronized {
        players.find(_.id == player_id) match {
          case Some(player) => {
            player.performAction(actions)
          }
          case None => List()
        }
      }
    }
  }

  def timeLeft: Float = {
    duration - (Game.time - startTime)
  }

  def state: serializable.GameState = {
    serializable.GameState(
        id,
        tick,
        timeLeft,
        players.map(_.state),
        resources.state,
        grid.state,
        viruses.state
      )
  }

  def createPlayers: List[Player] = {
    playerIds.map { new Player(_, spawnPosition) }
  }

  def spawnPosition: Vector2 = {
    grid.randomPosition
  }
}
