# API
The base URL for the API is `/api/[version]/` and the current version is `1`. The current URL would be `/api/1/`

## HTTP codes
We use HTTP codes to provide the query state. Every successful query will return a `200`. Every error will return a standard error code.

## Response structure
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

**Response**
```json
[{
	"player_id": "string", 
	"name": "string",
	"score": "float"
}]
```

## Game
### Fetch a game state
`GET /game/:game_id`

**Response**
```json
{
    "id": "int",
    "tick": "int",
    "players": [
    {
        "id": "int",
        "name": "string",
        "total_mass": "int",
        "isActive": "boolean",
        "cells": [
        {
            "id": "int",
            "mass": "int",
            "radius": "int",
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
        "radius": "int",
        "mass": "int",
        "position": {
            "x": "float",
            "y": "float"
        }
    }]
}
```

### Create a game
`POST /game`

**Request**
```json
{
    "player_secret" : "string"
}
```
**Response**
```json
{
    "game_id": "int",
    "url": "string"
}
```

### Create an action
`POST /game/:game_id/action`

**Request**
```json
{
    "player_secret": "string",
    "actions": [
    {
        "cell_id": "int",
        "burst": "bool",
        "split": "bool",
        "trade": "int",
        "target": {
            "x": "float",
            "y": "float"
        }
    }]
}
```
**Response**
```json
{
    "data": "ok"
}
```

## Administrator
### Request structure
Every query will need to contain the key `administratorPassword` with the right password.
```json
{
    "administrator_password": "string",
    ...
}
```

### Launch competition
Note that putting running to `false` won't have any effect. Every time this will be called
the game thread will be restarted.

`PUT /admin/competition`

**Request**
```json
{
    "running": "boolean"
}
```
**Response**
```json
{
    "data": "ok"
}
```

### Set ranked game duration
This will set the next ranked game duration(seconds).

`PUT /admin/ranked`

**Request**
```json
{
    "duration": "int"
}
```
**Response**
```json
{
    "data": "ok"
}
```

### Create player
`POST /admin/player`

**Request**
```json
{
    "player_name": "string"
}
```
**Response**
```json
{
    "data": {
        "player_secret": "string"
    }
}
```

### Seed players
`POST /admin/player`

**Request**
```json
{
    "seed": "boolean"
}
```
**Response**
```json
{
    "data": "ok"
}
```
