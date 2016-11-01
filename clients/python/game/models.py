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
                [],    # TODO Player.parse
                [],    # TODO Resources.parse
                Map.parse(obj["map"]),
                []     # TODO Virus.parse
                )


class Map:
    def __init__(self, width, height):
        self.width = width
        self.height = height

    def parse(obj):
        return Map(obj["width"], obj["height"])


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
