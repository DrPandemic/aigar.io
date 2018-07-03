package io.aigar.game.serializable

import com.github.jpbetz.subspace.Vector2

/**
 * Serializable classes that represent the current state of a game. These are
 * the classes that will be sent over the network, so they should follow the
 * conventions of the API.
 */

case class Position(
  x: Float,
  y: Float
)
case class Dimensions(
  width: Int,
  height: Int
)
case class Cell(
  id: Int,
  mass: Int,
  radius: Int,
  position: Position,
  target: Position,
  burst: Boolean
)
case class Player(
  id: Int,
  name: String,
  total_mass: Integer,
  isActive: Boolean,
  cells: List[Cell]
)
case class Resources(
  regular: List[Vector2],
  silver: List[Vector2],
  gold: List[Vector2]
)
case class Virus(
  position: Position,
  mass: Int,
  radius: Int
)
case class GameState(
  id: Int,
  var paused: Boolean,
  var disabledLeaderboard: Boolean,
  multiplier: Int,
  tick: Int,
  timeLeft: Float,
  players: List[Player],
  resources: Resources,
  map: Dimensions,
  viruses: List[Virus]
)
