module.exports = class Map {
  constructor(width, height) {
    this.width = width;
    this.height = height;
  }

  static parse(payload) {
    return new Map(payload.width, payload.height);
  }
};
