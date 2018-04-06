# AIgar.io
## Development
### Docker
Install `docker` and `docker-compose`. Run
`docker-compose -f docker-compose.yml up` in the root folder.

### Without docker
#### Requirements
You need to have `sbt`, `nginx` and `node` installed.

To start the project you need to go in the `bin` folder
(yes this is actually important).

`./start.sh`
will build the assets, start nginx and start the game server.
The `-p` flag represents the absolute path of the `api` folder.

`./stop.sh`
will stop the nginx server.


## API
The [complete API](API.md) can be found in the repository.

## Admin
You can login to the admin at `aigar.io/web/adminLogin.html`. The password can
be found in the server logs.

## Seeding the project
### API
```bash
curl --request POST \
  --url http://localhost/api/1/admin/player \
  --header 'content-type: application/json' \
  --data '{
	"administrator_password": "${the password in the logs}",
	"seed": true
}'
```

### SBT
To seed the project you will need to start a console in the project
`sbt console`.
```
scala> import io.aigar.model.seed
scala> seed.seedPlayers
```
