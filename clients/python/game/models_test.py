from unittest import TestCase

from .models import Game, Map


class GameTests(TestCase):
    def test_parse(self):
        obj = {
                "id": 12,
                "tick": 123,
                "map": {
                    "width": 111,
                    "height": 222
                    }
                # TODO add more here
                }

        game = Game.parse(obj)

        self.assertEqual(12, game.id)
        self.assertEqual(123, game.tick)
        self.assertEqual(111, game.map.width)
        self.assertEqual(222, game.map.height)
        # TODO add more here


class MapTests(TestCase):
    def test_parse(self):
        obj = {
                "width": 123,
                "height": 321
                }

        map_ = Map.parse(obj)

        self.assertEqual(123, map_.width)
        self.assertEqual(321, map_.height)
