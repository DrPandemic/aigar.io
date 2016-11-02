from planar import Vec2


class Game:
    RankedGameId = 0

    def __init__(self, id_, tick, players, resources, map_, viruses):
        self.id = id_
        self.tick = tick
        self.players = players
        self.resources = resources
        self.map = map_
        self.viruses = viruses

    def parse(obj):
        return Game(
                obj["id"],
                obj["tick"],
                [Player.parse(player) for player in obj["players"]],
                Resources.parse(obj["resources"]),
                Map.parse(obj["map"]),
                []     # TODO Virus.parse
                )

    def __str__(self):
        return ("""
Tick: %d
Map: %s
Players:
  %s
Resources:
  %s""" % (self.tick,
           self.map,
           "\n  ".join([str(player) for player in self.players]),
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
                []  # TODO Cell.parse
                )

    def __str__(self):
        return ("%d '%s' %s mass=%d cells=[%s]" %
                (self.id, self.name,
                 "active" if self.active else "inactive",
                 self.total_mass,
                 ", ".join([str(cell) for cell in self.cells])))


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


def parse_vec2(obj):
    return Vec2(obj["x"], obj["y"])
