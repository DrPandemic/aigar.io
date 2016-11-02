# Python Client
## Requirements
- Python 3

## Setup
1. `pip install -r ./requirements.txt`
2. Rename `player.default.json` to `player.json` and replace the
   `player_secret` by the value that was given to you by the organiser

## Joining the Ranked Game
`python ./play.py`

## Running the Tests (developers of the game)
`python -m unittest discover -p "*_test.py"`

## Documentation
### Game
State of a game.

Attributes:
- `id`: Identifier of the game

- `tick`: How many updates the game has gone through so far

- `players`: List of players (`Player` objects) in the game

- `resources`: Resources in the game that can be collected to gain mass/points

- `map`: Dimensions of the map (`Map` object)

- `viruses`: List of viruses that split a cell when consumed

### Map
Dimensions of the map the players are on.

Attributes:
- `width`: Width of the map

- `height`: Height of the map

### Player
Owner and controller of cells in the game. As a programmer of an AI, you are a
`Player`.

Attributes:
- `id`: Identifier of the player

- `name`: Display name of the player in-game

- `cells`: List of `Cell` objects that the player owns and controls

- `total_mass`: Sum of the mass of the player's cells

- `active`: Whether the player is actively controlling their cells or not. If a
            player's AI is inactive for too long, this flag will become `False`
            and a dumb AI will take over the player's cells until the player
            becomes active once again.
