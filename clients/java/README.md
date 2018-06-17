# Java Client
## Requirements
- Java 8
- Maven

## Setup
1. In `src/main/resources`, rename `player.default.json` to `player.json` and replace
   `player_id`, `player_secret` and `api_url` by the values that were given to you by the
   organisers
2. `mvn install`

## Creating your AI
The only file you need to modify is `Ai.java` under `src/main/java/io/aigar`.

## Joining the Ranked Game
`java -jar target/Aigar-Java-Client-1.0.jar`

## Joining your private Game
The private game needs to already exist.
`java -jar target/Aigar-Java-Client-1.0.jar -j`

## Creating and joining your private game
Note that you can have a maximum of one private game at any given time.
If your private game already exists, it will destroy it and create a new one.
`java -jar target/Aigar-Java-Client-1.0.jar -c`