const Vector = require('victor');

module.exports = class Virus {
  constructor(position, mass, radius) {
    this.position = position;
    this.mass = mass;
    this.radius = radius;
  }

  static parse(payload) {
    return new Virus(
      Vector.fromObject(payload.position),
      payload.mass,
      payload.radius
    );
  }
};
