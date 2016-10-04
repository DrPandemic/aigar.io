# AIgar.io
## Development
### Linux & Windows
#### Requirements
You need to have `sbt`, `nginx` and `node` installed.

#### Linux
To start the project you need to go in the `bin` folder
(yes this is actually important).

`./start.sh -p "/home/my_user/Projects/ai-competition/api"`
will build the assets, start nginx and start the game server.
The `-p` flag represents the absolute path of the `api` folder.

`./stop.sh -p "/home/my_user/Projects/ai-competition/api"`
will stop the nginx server.

#### Windows
To start the project you need to go in the `bin` folder
(yes this is actually important).

`start start.bat -p "C:\my_user\Projects\ai-competition\api"`
will build the assets, start nginx and start the game server.
The `-p` flag represents the absolute path of the `api` folder.

`./stop.bat -p "C:\my_user\Projects\ai-competition\api"`
will stop the nginx server.


By default, nginx will be running on port 1337.
