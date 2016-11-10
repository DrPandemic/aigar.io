def update_game(api, game_id, step_fn):
    """
    Takes care of doing one update tick:
    - fetch the game state
    - call the player's AI
    - send actions chosen by AI

    :param api: API object to communicate with the server
    :param game_id: ID of the game that we're playing
    :param step_fn: function to call to execute the player's AI
    """
    game = api.fetch_game_state(game_id)

    step_fn(game)

    api.send_actions(game_id, [cell.actions() for cell in game.me.cells])
