const nodeFetch = require('node-fetch');
const urlJoin = require('url-join');

const Game = require('./game.js');

module.exports = class API {
  constructor(playerId, playerSecret, apiUrl, fetch = nodeFetch) {
    this.playerId = playerId;
    this.playerSecret = playerSecret;
    this.apiUrl = urlJoin(apiUrl, '/api/1/game/');
    this.fetch = fetch;
  }

  fetchGameState(gameId, playerId) {
    return this.fetch(urlJoin(this.apiUrl, gameId), {method: 'get'})
      .then(res => res.json())
      .then(res => Game.parse(res.data, playerId));
  }

  sendActions(gameId, cellActions) {
    const data = {
      player_secret: this.playerSecret,
      actions: cellActions
    };

    return this.fetch(
      urlJoin(this.apiUrl, gameId, '/action'),
      {
        method: 'post',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
      });
  }

  createPrivate() {
    const body = {
      player_secret: this.playerSecret
    };
    return this.fetch(
      this.apiUrl,
      {
        method: 'post',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(body)
      });
  }
};
