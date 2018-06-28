# AIgar.io
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
