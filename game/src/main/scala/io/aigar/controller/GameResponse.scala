package io.aigar.controller.response

case class Position(
  x: Float,
  y: Float
)
case class Direction(
  x: Float,
  y: Float
)
case class Size(
  width: Int,
  height: Int
)
case class Cell(
  id: Int,
  mass: Int,
  position: Position,
  direction: Direction
)
case class Player(
  id: Int,
  name: String,
  total_mass: Integer,
  cells: List[Cell]
)
case class Food(
  regular: List[Position],
  silver: List[Position],
  gold: List[Position]
)
case class GameState(
  id: Int,
  tick: Int,
  players: List[Player],
  food: Food,
  map: Size,
  viruses: List[Position]
)
case class GameStateResponse(data: GameState)

case class GameCreation(id: Int, url: String)
case class GameCreationResponse(data: GameCreation)

case class SuccessResponse(data: String)

case class LeaderboardEntry(team_id: Int, name: String, score: Int)
case class LeaderboardResponse(data: List[LeaderboardEntry])

case class Action(
  cell_id: Int,
  burst: Boolean,
  split: Boolean,
  feed: Boolean,
  trade: Int,
  target: Position
)
case class ActionQuery(team_secret: String, actions: List[Action])
