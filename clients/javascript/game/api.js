module.exports = class API {
  constructor(playerId, playerSecret, apiUrl, fetch) {
    this.playerId = playerId;
    this.playerSecret = playerSecret;
    this.apiUrl = apiUrl;
    this.fetch = fetch;
  }

  fetchGameState(gameId) {
    return {};
  }

  sendActions(gameId, cellActions) {

  }

  createPrivate() {
    return 1;
  }
};
