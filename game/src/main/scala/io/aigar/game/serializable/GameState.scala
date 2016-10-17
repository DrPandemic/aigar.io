package io.aigar.game.serializable

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
  position: Position,
  target: Position
)
case class Player(
  id: Int,
  name: String,
  total_mass: Integer,
  cells: List[Cell]
)
case class Resources(
  regular: List[Position],
  silver: List[Position],
  gold: List[Position]
)
case class GameState(
  id: Int,
  tick: Int,
  players: List[Player],
  food: Resources,
  map: Dimensions,
  viruses: List[Position]
)
