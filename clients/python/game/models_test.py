from unittest import TestCase

from .models import Game, Player, Map


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
