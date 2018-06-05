def update_game(api, game, step_fn):
    """
    Takes care of doing one update tick:
    - call the player's AI
    - send actions chosen by AI

    :param api:     API object to communicate with the server
    :param game:    The current game's state.
    :param step_fn: function to call to execute the player's AI
    """
    step_fn(game)

    api.send_actions(game.id, list(filter(None.__ne__, [cell.actions() for cell in game.me.cells])))
