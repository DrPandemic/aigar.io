class API {
  constructor(playerId, playerSecret, apiUrl) {
    this.playerId = playerId;
    this.playerSecret = playerSecret;
    this.apiUrl = apiUrl;
  }

  createPrivate() {
    return 1;
  }

  fetchGameState(gameId) {
    return {};
  }
}

module.exports = API;
