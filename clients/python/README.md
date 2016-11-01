# Python Client
## Requirements
- Python 3

## Setup
`pip install -r requirements.txt`

## Joining the Ranked Game
`python play.py`

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

- `map`: Dimensions of the map (`Map` object)

- `viruses`: List of viruses that split a cell when consumed

### Map
Dimensions of the map the players are on.

Attributes:
- `width`: Width of the map

- `height`: Height of the map
