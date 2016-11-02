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

#### Attributes
- `id`: Identifier of the game

- `tick`: How many updates the game has gone through so far

- `players`: List of players (`Player` objects) in the game

- `resources`: Resources in the game that can be collected to gain mass/points

- `map`: Dimensions of the map (`Map` object)

- `viruses`: List of viruses that split a cell when consumed

### Map
Dimensions of the map the players are on.

#### Attributes
- `width`: Width of the map

- `height`: Height of the map

### Player
Owner and controller of cells in the game. As a programmer of an AI, you are a
`Player`.

#### Attributes
- `id`: Identifier of the player

- `name`: Display name of the player in-game

- `cells`: List of `Cell` objects that the player owns and controls

- `total_mass`: Sum of the mass of the player's cells

- `active`: Whether the player is actively controlling their cells or not. If a
            player's AI is inactive for too long, this flag will become `False`
            and a dumb AI will take over the player's cells until the player
            becomes active once again.

### Resources
Contains the information about the resources available on the map. Collecting
those resources with a cell results in a potential increase of a cell's mass
and a player's overall score in the competition.

There are three available resource types:
| Resource Type | Cell Mass Gain | Player Score Gain |
| ------------- | -------------- | ----------------- |
| `regular`     | 1              | 1                 |
| `silver`      | 3              | 3                 |
| `gold`        | 0              | 10                |

#### Attributes
- `regular`: List of positions (`Vec2` objects) for *regular* resources

- `silver`: List of positions (`Vec2` objects) for *silver* resources

- `gold`: List of positions (`Vec2` objects) for *gold* resources

### Vec2
A 2D vector from the [`planar`](https://pypi.python.org/pypi/planar) Python
library. Refer to
[its documentation](http://pythonhosted.org/planar/vectorref.html#planar.Vec2)
for details.

#### Interesting Methods
- `distance_to`: Calculates the distance between two `Vec2`s

- `almost_equals`: Check if two `Vec2`s have similar values (helps with
                   floating point equality comparisons)

- `angle_to`: Gives the smallest angle between two `Vec2`s (can be used to
              compare the directions of two cells, for example)
