# AIgar.io
## Development
### Docker
Install `docker` and `docker-compose`. Run
`docker-compose -f docker-compose.yml up` in the root folder.

## API
The [complete API](API.md) can be found in the repository.

## Admin
You can login to the admin at https://127.0.0.1/web/adminLogin.html. The password can
be found in the server logs.

## Seeding the project
### Admin
The database can be seeded from the web admin panel at https://127.0.0.1/adminLogin.html .

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
You can also seed the project by opening a SBT console.
`sbt console`.
```
scala> import io.aigar.model.seed
scala> seed.seedPlayers
```
