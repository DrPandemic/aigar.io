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

- `players`: List of players in the game

- `resources`: Resources in the game that can be collected to gain mass/points

- `map`: Dimensions of the map

- `viruses`: List of viruses that split a cell when consumed
