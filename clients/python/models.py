class Game:
    def __init__(self, id_, tick, players, resources, map_, viruses):
        self.id = id_
        self.tick = tick
        self.players = players
        self.resources = resources
        self.map = map_
        self.viruses = viruses

    def parse(obj):
        return Game(
                int(obj["id"]),
                int(obj["tick"]),
                [],    # TODO Player.parse(obj["players"])
                [],    # TODO Resources.parse(obj["resources"])
                None,  # TODO Map.parse(obj["map"])
                []     # TODO Viruses.parse(obj["viruses"])
                )
