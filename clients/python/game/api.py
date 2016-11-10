from requests import get, post

from .models import Game


class API:
    URL = "http://localhost:1337/api/1/game/"

    def __init__(self, player_id, player_secret):
        self.player_id = player_id
        self.player_secret = player_secret

    def fetch_game_state(self, game_id):
        """
        Fetches the Game state for the given game ID.

        :param game_id: ID of a game
        :returns:       Game object
        """
        response = get("%s%d" % (API.URL, game_id))
        data = self._extract_data(response)
        return Game.parse(data, self.player_id)

    def send_actions(self, game_id, cell_actions):
        data = {
                "player_secret": self.player_secret,
                "actions": [{
                    "cell_id": actions.cell_id,
                    "burst": actions.burst,
                    "split": actions.split,
                    "trade": actions.trade,
                    "target": {"x": actions.target.x, "y": actions.target.y}
                    } for actions in cell_actions]
                }

        post("%s%d/action" % (API.URL, game_id), json=data)

    def _extract_data(self, response):
        """
        Extracts the application data from a requests Response object.
        """
        return response.json()["data"]


class CellActions:
    def __init__(self, cell_id, target, burst, split, trade):
        self.cell_id = cell_id
        self.target = target
        self.burst = burst
        self.split = split
        self.trade = trade
