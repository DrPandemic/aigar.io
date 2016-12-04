from planar import Vec2


class UnknownPlayerIdException(Exception):
    def __init__(self, player_id):
        super().__init__("Unknown Player ID %d" % player_id)


class Game:
    RANKED_GAME_ID = -1

    def __init__(self, id_, tick, player_id, players,
                 resources, map_, viruses):
        self.id = id_
        self.tick = tick
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

    def parse(obj, player_id):
        return Game(
                obj["id"],
                obj["tick"],
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
    def __init__(self, width, height):
        self.width = width
        self.height = height

    def parse(obj):
        return Map(obj["width"], obj["height"])

    def __str__(self):
        return "%d x %d" % (self.width, self.height)


class Player:
    def __init__(self, id_, name, total_mass, active, cells):
        self.id = id_
        self.name = name
        self.total_mass = total_mass
        self.active = active
        self.cells = cells

    def parse(obj):
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
    def __init__(self, id_, mass, radius, position, target):
        self.id = id_
        self.mass = mass
        self.radius = radius
        self.position = position
        self.target = target

        self._actions = CellActions(self.id)

    def move(self, target):
        self.target = target

    def split(self):
        self._actions.split = True

    def burst(self):
        self._actions.burst = True

    def trade(self, quantity):
        self._actions.trade = quantity

    def parse(obj):
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
        actions = self._actions
        actions.target = self.target

        self._actions = CellActions(self.id)

        return actions


class Resources:
    def __init__(self, regular_positions, silver_positions, gold_positions):
        self.regular = regular_positions
        self.silver = silver_positions
        self.gold = gold_positions

    def parse(obj):
        return Resources(
                [parse_vec2(pos) for pos in obj["regular"]],
                [parse_vec2(pos) for pos in obj["silver"]],
                [parse_vec2(pos) for pos in obj["gold"]]
                )

    def __str__(self):
        return ("regular: %d, silver: %d, gold: %d" %
                (len(self.regular), len(self.silver), len(self.gold)))


class Virus:
    def __init__(self, mass, position):
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
    def __init__(self, cell_id):
        self.cell_id = cell_id
        self.target = None
        self.burst = False
        self.split = False
        self.trade = 0


def parse_vec2(obj):
    return Vec2(obj["x"], obj["y"])


def format_vec2(vec2):
    return "(%d,%d)" % (vec2.x, vec2.y)
