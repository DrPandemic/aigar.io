"""
This is the file that should be used to code your AI.
"""


class AI:
    def __init__(self):
        pass

    def step(self, game):
        """
        Given the state of the 'game', decide what your cells ('game.me.cells')
        should do.

        :param game: Game object
        """

        resources = (game.resources.gold + game.resources.silver +
                     game.resources.regular)

        for cell in game.me.cells:
            def distance_to_me(position):
                return cell.position.distance_to(position)

            if resources:
                closest = sorted(resources, key=distance_to_me)[0]
                cell.move(closest)

            cell.split()
