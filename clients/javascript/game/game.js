const PlayerError = require('./errors.js').UnknownPlayerIdException;
const Player = require('./player.js');
const Map = require('./map.js');
const Resources = require('./resources.js');
const Virus = require('./virus.js');

class Game {
  constructor(playerId, id, tick, timeLeft, players, resources, map, viruses) {
    this.id = id;
    this.tick = tick;
    this.timeLeft = timeLeft;
    this.players = players;
    this.resources = resources;
    this.map = map;
    this.viruses = viruses;

    this.me = players.find(p => p.id === playerId);
    if(this.me === undefined) {
      throw new PlayerError();
    }

    this.enemies = players.filter(p => p.id !== playerId);
  }

  static parse(payload, playerId) {
    return new Game(
      playerId,
      payload.id,
      payload.tick,
      payload.timeLeft,
      payload.players.map(p => Player.parse(p)),
      Resources.parse(payload.resources),
      Map.parse(payload.map),
      payload.viruses.map(v => Virus.parse(v))
    );
  }

  static get RANKED_GAME_ID() {
    return -1;
  }

  static get UPDATE_PER_SECOND() {
    return 3;
  }

  get actions() {
    return this.me.actions;
  }
}

module.exports = Game;
