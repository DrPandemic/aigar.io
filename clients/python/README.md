# Python Client
## Requirements
- Python 3

## Setup
### Linux
1. `pip install -r ./requirements.txt`
2. Rename `player.default.json` to `player.json` and replace
   `player_id`, `player_secret` and `api_url` by the values that were given to you by the
   organisers

### Windows
1. Download and extract the planar library at
`https://bitbucket.org/caseman/planar/get/9f8615991edd.zip`
2. Go into the extracted folder and run `python setup.py build_py install --skip-build`
3. `pip install "requests>=2.4.2"`
4. In the Aigar.io project folder, rename `player.default.json` to `player.json` and
   replace `player_id`, `player_secret` and `api_url` by the values that were given to
   you by the organisers

## Joining the Ranked Game
`python ./play.py`

## Creating and joining your private game
Note that you can have a maximum of one private game at any given time.
If your private game already exists, it will destroy it and create a new one.
`python ./play.py --create-private` or `python ./play.py -c`

## Joining your private Game
The private game needs to already exist.
`python ./play.py --join-private` or `python ./play.py -j`

## Running the Tests (developers of the game)
`python -m unittest discover -p "*_test.py"`

## Documentation
**All objects attributes should be considered read-only. Use provided functions to interact with them.**

### Game
State of a game.

#### Attributes
- `id`: Identifier of the game

- `tick`: How many updates the game has gone through so far

- `time_left`: How many seconds are left to the round

- `players`: List of players (`Player` objects) in the game

- `resources`: Resources in the game that can be collected to gain mass/points

- `map`: Dimensions of the map (`Map` object)

- `viruses`: List of viruses (`Virus` objects) that split a cell when consumed

- `me`: Your `Player` instance

- `enemies`: List of the other `Player`s

### Map
Dimensions of the map the players are on.

#### Attributes
- `width`: Width of the map

- `height`: Height of the map

### Player
Owner and controller of cells in the game. As a programmer of an AI, you are a
`Player`.
You can access your player via `game.me`.

#### Attributes
- `id`: Identifier of the player

- `name`: Display name of the player in-game

- `cells`: List of `Cell` objects that the player owns and controls

- `total_mass`: Sum of the mass of the player's cells

- `active`: Whether the player is actively controlling their cells or not. If a
            player's AI is inactive for too long, this flag will become `False`
            and a dumb AI will take over the player's cells until the player
            becomes active once again.

### Cell
Individual entity controlled by a `Player`. Through movement, it can consume
resources and enemy cells to grow. It loses a portion of its mass over time.

A cell can be moved by calling `move(target)` on it.

To collect a resource, a cell must collide with it.

To eat an enemy cell, a cell must almost completely overlap its enemy and be
10% bigger than it.

#### Attributes
- `id`: Identifier of the cell

- `mass`: Mass of the cell.
          The bigger the mass, the bigger the cell, the slower the cell can move
          or accelerate.
          Mass decays over time (by a ratio of the current mass).
          The minimal mass of a cell is `20`.

- `radius`: Radius of the cell, influenced by its current mass

- `position`: Current position (`Vec2` object) of the cell in the `Map`

- `target`: Current target (`Vec2` object) that the cell should go for.
            MODIFYING THIS VALUE WILL NOT CHANGE THE TARGET. USE THE `move` FUNCTION.

- `burst`: If the cell is currently bursting.

#### Methods
*Note: the following methods will only have an effect when called on cells
       owned by your player.*

- `move(target)`: Moves towards the given `Vec2` target.
                  Convenience method that sets `cell.target`.

- `split()`: Splits the cell into two distinct cells with half their parent's
             mass. The new cells will later be available to control via the
             `cells` list in `Player`.
             The cell must be at least twice the minimum mass for this call to
             have an effect.

- `burst()`: Exchanges 4% of the cell's mass  to gain a temporary speed boost.
             If the cell is too small to afford the price, this call has no
             effect.

- `trade(mass)`: Trades a given amount of the cell's mass to gain a small gain
                 in the player's score (competition points) with a ratio of 
                 2:1.
                 If the given quantity is too large for the cell's mass, the
                 trade will only be based on how much the cell can afford.


### Resources
Contains the information about the resources available on the map. Collecting
those resources with a cell results in a potential increase of a cell's mass
and a player's overall score in the competition.

To collect a resource, a `Cell` must collide with it.

There are three available resource types:

| Resource Type | Cell Mass Gain | Player Score Gain |
| ------------- | -------------- | ----------------- |
| `regular`     | 1              | 0.1               |
| `silver`      | 2              | 1                 |
| `gold`        | 0              | 10                |

#### Attributes
- `regular`: List of positions (`Vec2` objects) for *regular* resources

- `silver`: List of positions (`Vec2` objects) for *silver* resources

- `gold`: List of positions (`Vec2` objects) for *gold* resources

- `allResources`: List of positions (`Vec2` objects) for *all three types* of resources

### Virus
Dangerous stationary cell on the map that, when eaten, causes the eating cell
to explode (split and lose mass). It will remove 40% of the cell's mass and force
it to split once and its `children` to split once.

A `Cell` will explode if it mostly overlaps a virus and has 10% more mass. It
is safe to hide under a virus if the cell is smaller.

#### Attributes
- `radius`: Radius of the virus, influenced by its current mass

- `mass`: Mass of the virus.
          A `Cell` eating the virus will explode if it is 10% bigger than this.

- `position`: Position (`Vec2` object) of the virus

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
