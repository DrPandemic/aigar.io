# Aigar.io
Aigar.io is an artificial intelligence competition strongly inspired by
[the agar.io game](https://agar.io). Aigar.io is a long-running competition with
more than 30 teams competing to accumulate as much points as possible during the
day. The winning team will be the one with the highest score at the end.

During the competition, there will be many rounds. A game will last 10 minutes. 
The organisers have the ability to change the duration of the rounds if necessary.

**Note**
The web visualizer doesn't fetch game states at the same rate as the game
produces them, this means that many displayed states are simply interpolation
between two states. This explains why sometime you seem to eat a cell from too
far. AI clients don't interpolate states.

**Note 2**
This documentation contains formulas. For most AIs, it will be safe to ignore
them.

# The Game
Every team controls cells on a map containing every other team. The map also
contains many resources and viruses. A resource can't move and gives rewards when
eaten. A virus can't move, but when eaten it causes harm to the cell.

At the beginning of a round, every team has one cell.

Clients are set to fetch game state and send commands **3 times per second**.

## Mass
A cell starts with a mass of 20 and can't have more than 1000. A cell always 
consumes mass. At every game tick we calculate the new mass with
`def decayedMass(mass, deltaSeconds) = mass * pow(0.997f, deltaSeconds)`. This
means that a larger cell will lose more mass than a smaller one.

## Radius
This is the formula we use to calculate the radius of a cell from its mass.
`def radius(mass) = 4f + sqrt(mass) * 3f`.

### Grid
The background pattern is a grid of 50x50 units. This means that a cell with a
radius of 25 should exactly fit in one square.

## Score
There's a difference between mass and score. On the round leaderboard we see
the sum of the masses of a team. On the competition leaderboard we see scores.
There are two things you can do to increase your score, eating resources and
trading mass. Both are explained in later sections.

You AI will still move if it's not connected, but it won't gain score.

During the competition, the organisers will increase the score modifier. This
means that the later rounds will produce more score than the first ones.

## Eating
When a cell collides with a resource, it will eat it an win rewards. There are
three available resource types:

| Resource Type | Cell Mass Gain | Team Points Gain   |
| ------------- | -------------- | ----------------- |
| `regular`     | 1              | 0.1               |
| `silver`      | 2              | 1                 |
| `gold`        | 0              | 10                |

A cell can also eat a cell from another team. To do so, a cell must almost
completely overlap its enemy and be 10% bigger than it. The winning cell steals
all the mass from the losing cell.

When a team doesn't have any remaining cells, the game will respawn a new cell for
this team with a mass of 20. 

## Virus
Viruses are scattered across the map, and you need to be careful to not eat them.
Every virus has a mass of 100. This means that if your cell is small enough you
can hide it inside a virus and it won't affect it.

If your cell is 10% bigger than the virus, it will eat it.
Eating a virus will remove 40% of the cell's mass. It will also force it to split once and
force its `children` to split once (you will then have 4 cells).

## Actions
A cell can perform many different actions. 

### Move
You can move a cell toward a destination by providing coordinates.

Changing direction is not instantaneous. The velocity needs to first adapt to
match the new trajectory.

**You really don't need to understand this code.**
This is some Scala code that explains how we calculate the position and why
changing direction is not instantaneous.
```scala
def steering: Vector2 = {
  val dir = target - position
  val targetVelocity = dir.normalize * maxSpeed
  targetVelocity - velocity
}

def movement(deltaSeconds: Float): Vector2 = {
  steering.truncate(maxSpeed) * deltaSeconds
}

def update(deltaSeconds: Float): Unit = {
  velocity += movement(deltaSeconds)
  position += velocity * deltaSeconds
}
```

We use this formula to calculate cells' maximum speed.
`def maximumSpeed(mass) = max(100 - mass * 0.05f, 50)`. This means that a bigger
cell will go slower than a smaller one.

### Split
This will split a cell into 2 equally massive cells. To be able to perform a
split the cell needs to have a mass over 20 and the team needs to have less than
10 cells.

### Burst
By sacrificing mass a cell can increase its maximum speed. For a cost of 4% of
its mass, the cell will burst for 0.25 second. A burst can't bring the cell
under 20 mass.

### Trade
Trading let you convert mass into points. The conversion rate is 1/2. This means
that for every 2 mass traded you'll gain 1 score. Everytime you initiate a trade,
you will lose control of the cell for 5 seconds. If it's still alive after the
delay the actual trade will be performed.

### Merge
By moving over a friendly cell, the two cells will merge and the masses will be
combined.
