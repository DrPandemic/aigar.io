from unittest import TestCase
from planar import Vec2

from .models import Game, Map, Player, Cell, Resources, Virus


class GameTests(TestCase):
    def test_parse(self):
        obj = {
                "id": 12,
                "tick": 123,
                "map": {
                    "width": 111,
                    "height": 222
                    },
                "players": [
                    {
                        "id": "a",
                        "name": "b",
                        "total_mass": 23,
                        "isActive": True,
                        "cells": []  # TODO once Cells are implemented
                        }
                    ],
                "resources": {
                    "regular": [{"x": 1, "y": 2}],
                    "silver": [{"x": 3, "y": 4}],
                    "gold": [{"x": 5, "y": 6}]
                    },
                "viruses": [{
                    "mass": 2,
                    "position": {"x": 1, "y": 2}
                    }]
                }

        game = Game.parse(obj)

        self.assertEqual(12, game.id)
        self.assertEqual(123, game.tick)

        self.assertEqual(111, game.map.width)
        self.assertEqual(222, game.map.height)

        self.assertEqual(1, len(game.players))
        player = game.players[0]
        self.assertEqual("a", player.id)
        self.assertEqual("b", player.name)
        self.assertEqual(23, player.total_mass)
        self.assertEqual(True, player.active)

        self.assertEqual(1, len(game.resources.regular))
        self.assertTrue(game.resources.regular[0].almost_equals(Vec2(1, 2)))
        self.assertEqual(1, len(game.resources.silver))
        self.assertTrue(game.resources.silver[0].almost_equals(Vec2(3, 4)))
        self.assertEqual(1, len(game.resources.gold))
        self.assertTrue(game.resources.gold[0].almost_equals(Vec2(5, 6)))

        self.assertEqual(1, len(game.viruses))
        virus = game.viruses[0]
        self.assertEqual(2, virus.mass)
        self.assertTrue(virus.position.almost_equals(Vec2(1, 2)))


class MapTests(TestCase):
    def test_parse(self):
        obj = {
                "width": 123,
                "height": 321
                }

        map_ = Map.parse(obj)

        self.assertEqual(123, map_.width)
        self.assertEqual(321, map_.height)


class PlayerTests(TestCase):
    def test_parse(self):
        obj = {
                "id": "bob",
                "name": "alice",
                "total_mass": 42,
                "isActive": True,
                "cells": {}  # TODO once Cells are implemented
                }

        player = Player.parse(obj)

        self.assertEqual("bob", player.id)
        self.assertEqual("alice", player.name)
        self.assertEqual(42, player.total_mass)
        self.assertEqual(True, player.active)
        # TODO test cell equality here


class CellTests(TestCase):
    def test_parse(self):
        obj = {
                "id": 1,
                "mass": 12,
                "radius": 8,
                "position": {"x": 1241, "y": 442},
                "target": {"x": 1448, "y": 1136}
                }

        cell = Cell.parse(obj)

        self.assertEqual(1, cell.id)
        self.assertEqual(12, cell.mass)
        self.assertEqual(8, cell.radius)
        self.assertTrue(cell.position.almost_equals(Vec2(1241, 442)))
        self.assertTrue(cell.target.almost_equals(Vec2(1448, 1136)))


class ResourcesTests(TestCase):
    def test_parse(self):
        obj = {
                "regular": [{"x": 1, "y": 2}],
                "silver": [{"x": 3, "y": 4}],
                "gold": [{"x": 5, "y": 6}]
                }

        resources = Resources.parse(obj)

        self.assertEqual(1, len(resources.regular))
        self.assertTrue(resources.regular[0].almost_equals(Vec2(1, 2)))

        self.assertEqual(1, len(resources.silver))
        self.assertTrue(resources.silver[0].almost_equals(Vec2(3, 4)))

        self.assertEqual(1, len(resources.gold))
        self.assertTrue(resources.gold[0].almost_equals(Vec2(5, 6)))


class VirusTests(TestCase):
    def test_parse(self):
        obj = {
                "mass": 1,
                "position": {"x": 2, "y": 3}
                }

        virus = Virus.parse(obj)

        self.assertEqual(1, virus.mass)
        self.assertTrue(virus.position.almost_equals(Vec2(2, 3)))
