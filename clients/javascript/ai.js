const Vector = require('victor');

module.exports = class AI {
  constructor() {}

  step(game) {
    for(const cell of game.me.cells) {
      const distance = cell.position.distance(cell.target);
      // Is close to its end
      if (distance <= 10) {
        const target = (new Vector(0, 0))
          .randomize(new Vector(0, 0), new Vector(game.map.width, game.map.height));
        cell.move(target);
      }
    }

    return game;
  }
};
