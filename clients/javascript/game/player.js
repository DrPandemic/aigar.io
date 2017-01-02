const Cell = require('./cell.js');

module.exports = class Player {
  constructor(id, name, totalMass, active, cells) {
    this.id = id;
    this.name = name;
    this.totalMass = totalMass;
    this.active = active;
    this.cells = cells;
  }

  static parse(payload) {
    return new Player(
      payload.id,
      payload.name,
      payload.total_mass,
      payload.isActive,
      payload.cells.map(c => Cell.parse(c))
    );
  }

  get actions() {
    return this.cells.map(c => c.actions());
  }
};
