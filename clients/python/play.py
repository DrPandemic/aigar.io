from time import sleep
import json

from game.api import API
from game.models import Game


UpdatesPerSecond = 3  # how many times we should contact the server per second


def main():
    game_id = Game.RankedGameId

    team_secret = fetch_team_secret()
    api = API(team_secret)

    while True:
        game = api.fetch_game_state(game_id)

        # TODO call player AI here
        print(game.tick)

        sleep(1 / UpdatesPerSecond)


def fetch_team_secret():
    NullTeamSecret = "REPLACEME"  # value that is by default in the config file

    with open("player.json") as f:
        data = json.load(f)
        secret = data["team_secret"]

        if secret == NullTeamSecret:
            print("WARNING: Did you forget to change your team secret in "
                  "'player.json'?")

        return secret


if __name__ == "__main__":
    main()
