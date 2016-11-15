# AIgar.io
## Development
### Docker
Install `docker` and `docker-compose`. Run `docker-compose up` in the root folder.

### Linux & Windows
#### Requirements
You need to have `sbt`, `nginx` and `node` installed.

#### Linux
To start the project you need to go in the `bin` folder
(yes this is actually important).

`./start.sh`
will build the assets, start nginx and start the game server.
The `-p` flag represents the absolute path of the `api` folder.

`./stop.sh`
will stop the nginx server.

#### Windows
To start the project you need to go in the `bin` folder
(yes this is actually important).

`start start.bat -p "C:\my_user\Projects\ai-competition\api"`
will build the assets, start nginx and start the game server.
The `-p` flag represents the absolute path of the `api` folder.

`start stop.bat -p "C:\my_user\Projects\ai-competition\api"`
will stop the nginx server.


By default, nginx will be running on port 1337.

## API
The [complete API](API.md) can be found in the repository.

## Seeding the project
To seed the project you will need to start a console in the project `sbt console`.
```
scala> import io.aigar.model.seed
scala> seed.seedPlayers
```
