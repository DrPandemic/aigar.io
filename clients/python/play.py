from time import sleep

from game.api import API
from game.models import Game


UpdatesPerSecond = 3  # how many times we should contact the server per second


def main():
    game_id = Game.RankedGameId
    api = API("TODO")

    while True:
        game = api.fetch_game_state(game_id)

        # TODO call player AI here
        print("Tick: %d" % game.tick)
        print("Map: %d by %d" % (game.map.width, game.map.height))
        print()

        sleep(1 / UpdatesPerSecond)


if __name__ == "__main__":
    main()
