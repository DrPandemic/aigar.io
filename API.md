# API
The base URL for the API is `/api/[version]/` and the current version is `1`. The current URL would be `/api/1/`

## HTTP codes
We use HTTP codes to provide the query state. Every successful query will return a `200`. Every error will return a standard error code.

## Structure
Every successful response will contain a root element named `data`.
```json
{
  "data": "something"
}
```
Every error will contain a root element named `error` with an error message.
```json
{
  "error": "string"
}
```

## Main leaderboard
### Fetch the global score
`GET /leaderboard`
```json
[{
	"player_id": "string", 
	"name": "string",
	"score": "int"
}]
```

## Game
### Fetch a game state
`GET /game/:game_id`
```json
{
    "id": "int",
    "tick": "int",
    "players": [
    {
        "id": "string",
        "name": "string",
        "total_mass": "int",
        "isActive": "boolean",
        "cells": [
        {
            "id": "int",
            "mass": "int",
            "position": {
                "x": "float",
                "y": "float"
            },
            "target": {
                "x": "float",
                "y": "float"
            }
        }]
    }],
    "resources": {
        "regular": {
            "position": {
                "x": "float",
                "y": "float"
            }
        },
        "silver": {
            "position": {
                "x": "float",
                "y": "float"
            }
        },
        "gold": {
            "position": {
                "x": "float",
              "y": "float"
            }
        }
    },
    "map": {
        "width": "int",
        "height": "int"
    },
    "viruses": [
    {
        "position": {
            "x": "float",
            "y": "float"
        }
    }]
}
```

### Create a game
`POST /game` <- `{"player_secret" : "string"}`
```json
{
    "game_id": "int",
    "url": "string"
}
```

### Create an action
`POST/game/:game_id/action` <-
```json
{
    "player_secret": "string",
    "actions": [
    {
        "cell_id": "int",
        "burst": "bool",
        "split": "bool",
        "feed": "bool",
        "trade": "int",
        "target": {
            "x": "float",
            "y": "float"
        }
    }]
}
```

```json
{
    "data": "ok"
}
```
