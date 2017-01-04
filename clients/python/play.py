from time import sleep
import json
import sys

from game.game_loop import update_game
from game.api import API
from game.models import Game
from ai import AI


UpdatesPerSecond = 3  # how many times we should contact the server per second


def main():
    create_private = "--create-private" in sys.argv or "-c" in sys.argv
    join_private = "--join-private" in sys.argv or "-j" in sys.argv

    player_id, player_secret, api_url = read_config()
    game_id = player_id if join_private else Game.RANKED_GAME_ID
    api = API(player_id, player_secret, api_url)
    previous_tick = -1
    ai = AI()

    if(create_private):
        game_id = api.create_private()
        # This is useful since the game creation is not instant
        sleep(0.5)

    while True:
        game = api.fetch_game_state(game_id)

        if(game.tick < previous_tick):  # After a game reset, it reinstanciates the AI object
            ai = AI()
        previous_tick = game.tick

        update_game(api, game, ai.step)

        sleep(1 / UpdatesPerSecond)


def read_config():
    ConfigFile = "player.json"
    DefaultConfigFile = "player.default.json"
    DefaultConfigValue = "REPLACEME"  # default value in the config file

    try:
        with open(ConfigFile) as f:
            data = json.load(f)
            id_ = data["player_id"]
            secret = data["player_secret"]
            api_url = data["api_url"]

            if id_ == DefaultConfigValue or secret == DefaultConfigValue or api_url == DefaultConfigValue:
                print("WARNING: Did you forget to change your player "
                      " id/secret/api_url in '%s'?" % ConfigFile, file=sys.stderr)

            return int(id_), secret, api_url

    except FileNotFoundError:
        print("ERROR: Could not find '%s'. "
              "Did you forget to rename '%s' to '%s'?" %
              (ConfigFile, DefaultConfigFile, ConfigFile), file=sys.stderr)
        exit(1)


if __name__ == "__main__":
    main()
