class Game {
  constructor() {
  }

  static parse(payload) {
    return new Game();
  }

  static get RANKED_GAME_ID() {
    return -1;
  }

  static get UPDATE_PER_SECOND() {
    return 3;
  }

  get actions() {
    return {};
  }
}

module.exports = Game;
