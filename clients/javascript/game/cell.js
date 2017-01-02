module.exports = class Cell {
  constructor(position) {
    this.position = position;
  }

  static parse(payload) {
    return new Cell();
  }
};
