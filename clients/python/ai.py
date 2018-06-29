"""
This is the file that should be used to code your AI.
"""
import random
from planar import Vec2


class AI:
    def __init__(self):
        pass

    def step(self, game):
        """
        Given the state of the 'game', decide what your cells ('game.me.cells')
        should do.

        :param game: Game object
        """

        for cell in game.me.cells:
            distance = cell.position.distance_to(cell.target)

            if distance < 10:
                target = Vec2(random.randint(0, game.map.width), random.randint(0, game.map.height))
                cell.move(target)
