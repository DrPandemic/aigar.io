from planar import Vec2
from typing import List


class UnknownPlayerIdException(Exception):
    def __init__(self, player_id):
        super().__init__("Unknown Player ID %d" % player_id)


class Game:
    RANKED_GAME_ID = -1

    def __init__(self, id_: int, tick: int, time_left: float, player_id: int, players: List['Player'],
                 resources: 'Resources', map_: 'Map', viruses: List['Virus']):
        self.id = id_
        self.tick = tick
        self.time_left = time_left
        self.players = players
        self.resources = resources
        self.map = map_
        self.viruses = viruses

        try:
            self.me = next(player for player in players
                           if player.id == player_id)
        except StopIteration:
            raise UnknownPlayerIdException(player_id)

        self.enemies = [player for player in players if player.id != player_id]

    def parse(obj, player_id) -> 'Game':
        return Game(
                obj["id"],
                obj["tick"],
                obj["timeLeft"],
                player_id,
                [Player.parse(player) for player in obj["players"]],
                Resources.parse(obj["resources"]),
                Map.parse(obj["map"]),
                [Virus.parse(virus) for virus in obj["viruses"]]
                )

    def __str__(self):
        return ("""
Tick: %d
Map: %s
Players:
  %s
Viruses: [%s]
Resources:
  %s""" % (self.tick,
           self.map,
           "\n  ".join([str(player) for player in self.players]),
           ", ".join([str(virus) for virus in self.viruses]),
           self.resources))


class Map:
    def __init__(self, width: float, height: float):
        self.width = width
        self.height = height

    def parse(obj) -> 'Map':
        return Map(obj["width"], obj["height"])

    def __str__(self):
        return "%d x %d" % (self.width, self.height)


class Player:
    def __init__(self, id_: int, name: str, total_mass: int, active: bool, cells: List['Cell']):
        self.id = id_
        self.name = name
        self.total_mass = total_mass
        self.active = active
        self.cells = cells

    def parse(obj) -> 'Player':
        return Player(
                obj["id"],
                obj["name"],
                obj["total_mass"],
                obj["isActive"],
                [Cell.parse(cell) for cell in obj["cells"]]
                )

    def __str__(self):
        return ("%d '%s' %s mass=%d cells=[%s]" %
                (self.id, self.name,
                 "active" if self.active else "inactive",
                 self.total_mass,
                 ", ".join([str(cell) for cell in self.cells])))


class Cell:
    def __init__(self, id_: int, mass: int, radius: float, position: Vec2, target: Vec2):
        self.id = id_
        self.mass = mass
        self.radius = radius
        self.position = position
        self.target = target

        self._actions = CellActions(self.id)

    def move(self, target: Vec2):
        self._actions.target = target

    def split(self):
        self._actions.split = True

    def burst(self):
        self._actions.burst = True

    def trade(self, quantity: int):
        self._actions.trade = quantity

    def parse(obj) -> 'Cell':
        return Cell(
                obj["id"],
                obj["mass"],
                obj["radius"],
                parse_vec2(obj["position"]),
                parse_vec2(obj["target"])
                )

    def __str__(self):
        return ("#%d (%d) %s -> %s" %
                (self.id, self.mass,
                 format_vec2(self.position), format_vec2(self.target)))

    def actions(self):
        actions = self._actions.export()

        if actions is not None and actions.target is None:
            actions.target = self.target

        return actions


class Resources:
    def __init__(self, regular_positions: List[Vec2], silver_positions: List[Vec2], gold_positions: List[Vec2]):
        self.regular = regular_positions
        self.silver = silver_positions
        self.gold = gold_positions
        self.allResources = regular_positions + silver_positions + gold_positions

    def parse(obj) -> 'Resources':
        return Resources(
                [parse_vec2(pos) for pos in obj["regular"]],
                [parse_vec2(pos) for pos in obj["silver"]],
                [parse_vec2(pos) for pos in obj["gold"]]
                )

    def __str__(self):
        return ("regular: %d, silver: %d, gold: %d" %
                (len(self.regular), len(self.silver), len(self.gold)))


class Virus:
    def __init__(self, mass: int, position: Vec2):
        self.mass = mass
        self.position = position

    def parse(obj):
        return Virus(
                obj["mass"],
                parse_vec2(obj["position"])
                )

    def __str__(self):
        return format_vec2(self.position)


class CellActions:
    def __init__(self, cell_id: int):
        self._cell_id = cell_id
        self._target = None
        self._burst = False
        self._split = False
        self._trade = 0
        self._changed = False

    @property
    def cell_id(self) -> int:
        return self._cell_id

    @cell_id.setter
    def cell_id(self, c):
        self._cell_id = c
        self._changed = True

    @property
    def target(self) -> Vec2:
        return self._target

    @target.setter
    def target(self, t):
        self._target = t
        self._changed = True

    @property
    def burst(self):
        return self._burst

    @burst.setter
    def burst(self, b):
        self._burst = b
        self._changed = True

    @property
    def split(self):
        return self._split

    @split.setter
    def split(self, s):
        self._split = s
        self._changed = True

    @property
    def trade(self):
        return self._trade

    @trade.setter
    def trade(self, t):
        self._trade = t
        self._changed = True

    def export(self) -> 'CellActions':
        if not self._changed:
            return None
        return self


def parse_vec2(obj):
    if obj["x"] is None or obj["y"] is None:
        return Vec2(0, 0)
    return Vec2(obj["x"], obj["y"])


def format_vec2(vec2):
    return "(%d,%d)" % (vec2.x, vec2.y)
