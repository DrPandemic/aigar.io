# AIgar.io
## What is it?
AIgar.io is an artificial intelligence competition written to be used at
Sherbrooke University by its
[computer science student group](https://github.com/jdis).

![image](https://user-images.githubusercontent.com/3250155/43427356-30a688aa-9427-11e8-8da4-59d310e9a212.png)

![image](https://user-images.githubusercontent.com/3250155/43427370-3957d42c-9427-11e8-9ccf-390dbf434999.png)

The project contains:
- Game server
- Game visualizer
- Leaderboard
- Admin panel
- API for bots
- 3 SDKs (Javascript, Python and Java)

The project is healthy enough to be used in a competition event. If you want to
use this project and want more information, feel free to contact @DrPandemic.

## Events
It was organized for the first time on July 14th 2018. There was 30 teams and
everything went mostly well. We had an issue with bandwidth pressure created by
the leaderboard, but that solved in the following week. The competition lasted 8
hours.

## Development
### Docker
Install `docker` and `docker-compose`. Run
`docker-compose -f docker-compose.yml up` in the root folder.

## Production
Install `docker` and `docker-compose`. Run
`docker-compose -f docker-compose.prod.yml up` in the root folder.

Be patient, generating the TLS certificate can be slow (multiple minutes).

## API
The [complete API](API.md) can be found in the repository.

## Documentation
The [user documentation](documentation.md) can be found in the repository.

## Admin
You can login to the admin at https://127.0.0.1/web/adminLogin.html. The password
can be found in the server's logs.

### Seed
In the danger zone, there's two options to seed the database. For development,
it is easier to seed to project with already 30 players. But, for a real
competition you should seed the database with no default player. Everytime you
seed the database, every data will be lost.

### Reset Thread
This launches the competition by starting the first game. During development,
you will need to reset the thread everytime you change some Scala code.

### Pause/Resume
This pause the current game. The goal of this feature is to let players eat
during the lunch break.

### Set Ranked Duration
This changes the next ranked game duration.

### Change Multiplier
It changes the score multiplier. The initial value is 1. The goal of this
feature is to give more importance to later rounds.

### Add Player
Creates a new player with the given name. It will display the 2 values needed
by the player (id and secret).

### Player Zone
It's a list of every players with their secrets.
