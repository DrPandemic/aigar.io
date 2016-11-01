from unittest import TestCase

from .models import Game


class GameTests(TestCase):
    def test_parse(self):
        obj = {
                "id": 12,
                "tick": 123,
                # TODO add more here
                }

        game = Game.parse(obj)

        self.assertEqual(12, game.id)
        self.assertEqual(123, game.tick)
        # TODO add more here
