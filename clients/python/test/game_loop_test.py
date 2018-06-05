from unittest import TestCase
from planar import Vec2

from game.game_loop import update_game
from game.models import Game, Cell, Player, Map, Resources


class GameLoopTests(TestCase):
    def test_update_game_calls_send_actions(self):
        cells = [Cell(0, 5, 10, Vec2(0, 0), Vec2(1, 1)),
                 Cell(1, 5, 10, Vec2(0, 0), Vec2(1, 1)),
                 Cell(2, 5, 10, Vec2(0, 0), Vec2(1, 1))]
        player = Player(0, "", 10, True, cells)
        game = Game(0, 0, 0, [player], Resources([], [], []), Map(0, 0), [])

        cells[0].target = Vec2(2, 2)
        cells[0].burst()

        cells[1].split()
        cells[1].trade(3)

        class MockApi:
            def send_actions(self, game_id, actions):
                self.actions = actions

        api = MockApi()

        update_game(api, game, lambda x: None)

        self.assertEqual(len(api.actions), 2)
        self.assertTrue(
            api.actions[0].target.almost_equals(Vec2(2, 2)))
        self.assertTrue(api.actions[0].burst)

        self.assertTrue(api.actions[1].split)
        self.assertEqual(3, api.actions[1].trade)
