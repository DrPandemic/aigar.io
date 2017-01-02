const Vector = require('victor');

const CellAction = require('./cell-action.js');

module.exports = class Cell {
  constructor(id, mass, radius, position, target) {
    this.id = id;
    this.mass = mass;
    this.radius = radius;
    this.position = position;
    this.target = target;

    this.actions_ = new CellAction(id);
  }

  static parse(payload) {
    return new Cell(
      payload.id,
      payload.mass,
      payload.radius,
      Vector.fromObject(payload.position),
      Vector.fromObject(payload.target)
    );
  }

  move(target) {
    this.target = target;
  }

  burst() {
    this.actions_.burst = true;
  }

  split() {
    this.actions_.split = true;
  }

  trade(t) {
    this.actions_.trade = t;
  }

  actions() {
    const actions = this.actions_;
    actions.target = this.target;

    this.actions_ = new CellAction(this.id);

    return actions;
  }
};
