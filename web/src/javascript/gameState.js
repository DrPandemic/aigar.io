export class Position {
  constructor(obj) {
    this.x = obj.x;
    this.y = obj.y;
  }
}
export class Dimensions {
  constructor(obj) {
    this.width = obj.width;
    this.height = obj.height;
  }
}
export class Cell {
  constructor(obj) {
    this.id = obj.id;
    this.mass = obj.mass;
    this.position = obj.position;
    this.target = obj.target;
  }
}
export class Player {
  constructor(obj) {
    this.id = obj.id;
    this.name = obj.name;
    this.totalMass = obj.totalMass;
    this.cells = obj.cells;
  }
}
export class Food {
  constructor(obj) {
    this.regular = obj.regular;
    this.silver = obj.silver;
    this.gold = obj.gold;
  }
}
export class GameState {
  constructor(obj) {
    this.id = obj.id;
    this.tick = obj.tick;
    this.players = obj.players;
    this.food = obj.food;
    this.map = obj.map;
    this.viruses = obj.viruses;
  }
}

function createPlayers()

export default function parseServerState(response) {
  const data = response.data;
  
}
