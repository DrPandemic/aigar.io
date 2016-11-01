from time import sleep
import json
import sys

from game.api import API
from game.models import Game


UpdatesPerSecond = 3  # how many times we should contact the server per second


def main():
    game_id = Game.RankedGameId

    player_secret = fetch_player_secret()
    api = API(player_secret)

    while True:
        game = api.fetch_game_state(game_id)

        # TODO call player AI here
        print(game.tick)

        sleep(1 / UpdatesPerSecond)


def fetch_player_secret():
    ConfigFile = "player.json"
    DefaultConfigFile = "player.default.json"
    NullPlayerSecret = "REPLACEME"  # default value in the config file

    try:
        with open(ConfigFile) as f:
            data = json.load(f)
            secret = data["player_secret"]

            if secret == NullPlayerSecret:
                print("WARNING: Did you forget to change your player secret "
                      "in '%s'?" % ConfigFile, file=sys.stderr)

            return secret

    except FileNotFoundError:
        print("ERROR: Could not find '%s'. "
              "Did you forget to rename '%s' to '%s'?" %
              (ConfigFile, DefaultConfigFile, ConfigFile), file=sys.stderr)
        exit(1)


if __name__ == "__main__":
    main()
