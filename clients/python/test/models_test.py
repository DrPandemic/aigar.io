from unittest import TestCase
from planar import Vec2

from game.models import (Game, Map, Player, Cell, Resources, Virus,
                         UnknownPlayerIdException)


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
                        "id": 1,
                        "name": "b",
                        "total_mass": 23,
                        "isActive": True,
                        "cells": [{
                            "id": 1,
                            "mass": 2,
                            "radius": 3,
                            "position": {"x": 1, "y": 2},
                            "target": {"x": 3, "y": 4}
                            }]
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

        game = Game.parse(obj, 1)

        self.assertEqual(12, game.id)
        self.assertEqual(123, game.tick)

        self.assertEqual(111, game.map.width)
        self.assertEqual(222, game.map.height)

        self.assertEqual(1, len(game.players))
        player = game.players[0]
        self.assertEqual(1, player.id)
        self.assertEqual("b", player.name)
        self.assertEqual(23, player.total_mass)
        self.assertEqual(True, player.active)

        self.assertEqual(1, len(player.cells))
        cell = player.cells[0]
        self.assertEqual(1, cell.id)
        self.assertEqual(2, cell.mass)
        self.assertEqual(3, cell.radius)
        self.assertTrue(cell.position.almost_equals(Vec2(1, 2)))
        self.assertTrue(cell.target.almost_equals(Vec2(3, 4)))

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

    def test_init(self):
        me = Player(123, "", 0, False, [])
        others = [
                Player(222, "", 0, False, []),
                Player(111, "", 0, False, [])
                ]
        game = Game(0, 0, me.id, [me] + others, None, None, [])

        self.assertEqual(me, game.me)
        self.assertEqual(others, game.enemies)

    def test_init_unknown_player_id(self):
        unknown_id = 42
        players = [
                Player(222, "", 0, False, []),
                Player(111, "", 0, False, [])
                ]
        self.assertRaises(UnknownPlayerIdException, Game,
                          0, 0, unknown_id, players, None, None, [])


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
                "cells": [{
                    "id": 1,
                    "mass": 2,
                    "radius": 3,
                    "position": {"x": 1, "y": 2},
                    "target": {"x": 3, "y": 4}
                    }]
                }

        player = Player.parse(obj)

        self.assertEqual("bob", player.id)
        self.assertEqual("alice", player.name)
        self.assertEqual(42, player.total_mass)
        self.assertEqual(True, player.active)

        self.assertEqual(1, len(player.cells))
        cell = player.cells[0]
        self.assertEqual(1, cell.id)
        self.assertEqual(2, cell.mass)
        self.assertEqual(3, cell.radius)
        self.assertTrue(cell.position.almost_equals(Vec2(1, 2)))
        self.assertTrue(cell.target.almost_equals(Vec2(3, 4)))


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

    def test_actions_sets_actions_target(self):
        cell = Cell(1, 2, 3, Vec2(4, 5), Vec2(6, 7))
        cell.move(Vec2(6, 7))

        actions = cell.actions()

        self.assertEqual(1, actions.cell_id)
        self.assertTrue(Vec2(6, 7).almost_equals(actions.target))

    def test_split_sets_actions_split(self):
        cell = Cell(1, 2, 3, Vec2(4, 5), Vec2(6, 7))

        cell.split()

        actions = cell.actions()
        self.assertTrue(actions.split)
        self.assertIsNotNone(actions.target)

    def test_burst_sets_actions_burst(self):
        cell = Cell(1, 2, 3, Vec2(4, 5), Vec2(6, 7))

        cell.burst()

        actions = cell.actions()
        self.assertTrue(actions.burst)
        self.assertIsNotNone(actions.target)

    def test_trade_sets_actions_trade(self):
        cell = Cell(1, 2, 3, Vec2(4, 5), Vec2(6, 7))

        cell.trade(42)

        actions = cell.actions()
        self.assertEqual(42, actions.trade)
        self.assertIsNotNone(actions.target)

    def test_actions_do_nothing(self):
        cell = Cell(1, 2, 3, Vec2(4, 5), Vec2(6, 7))

        actions = cell.actions()

        self.assertIsNone(actions)


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
