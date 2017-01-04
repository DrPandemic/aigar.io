const Vector = require('victor');

module.exports = class AI {
  constructor() {}

  step(game) {
    console.log(game.tick);
    game.me.cells[0].target = new Vector(0, 0);

    return game;
  }
};
