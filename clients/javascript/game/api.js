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

  async fetchGameState(gameId, playerId) {
    const response = await this.fetch(urlJoin(this.apiUrl, gameId), {method: 'get'});

    if (!response.ok) {
      throw await response.text();
    }

    return Game.parse((await response.json()).data, playerId);
  }

  sendActions(gameId, cellActions) {
    return this.fetch(
      urlJoin(this.apiUrl, gameId, '/action'),
      {
        method: 'post',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
          player_secret: this.playerSecret,
          actions: cellActions
        })
      });
  }

  createPrivate() {
    return this.fetch(
      this.apiUrl,
      {
        method: 'post',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
          player_secret: this.playerSecret
        })
      });
  }
};
