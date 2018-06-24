const Vector = require('victor');

module.exports = class AI {
  constructor() {}

  step(game) {
    const resources = game.resources.gold.concat(game.resources.silver)
      .concat(game.resources.regular);

    for(const cell of game.me.cells) {
      resources.sort((a, b) => {
        a = a.distanceSq(cell.position);
        b = b.distanceSq(cell.position);
        return a - b;
      });

      cell.move(resources[0].clone());
      cell.burst();
    }

    return game;
  }
};
