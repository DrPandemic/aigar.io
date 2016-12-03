from requests import get, post

from .models import Game


class API:
    def __init__(self, player_id, player_secret, api_url):
        self.player_id = player_id
        self.player_secret = player_secret
        self.api_url = api_url + "/api/1/game/"


    def fetch_game_state(self, game_id):
        """
        Fetches the Game state for the given game ID.

        :param game_id: ID of a game
        :returns:       Game object
        """
        response = get("%s%d" % (self.api_url, game_id))
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

        post("%s%d/action" % (self.api_url, game_id), json=data)

    def create_private(self):
        """
        Creates a private game.

        :returns:       The new game's ID
        """
        data = {
                "player_secret": self.player_secret
                }

        return self._extract_data(post(self.api_url, json=data))["id"]

    def _extract_data(self, response):
        """
        Extracts the application data from a requests Response object.
        """
        return response.json()["data"]
