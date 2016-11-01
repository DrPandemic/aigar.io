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
                None,  # TODO Map.parse
                []     # TODO Virus.parse
                )
