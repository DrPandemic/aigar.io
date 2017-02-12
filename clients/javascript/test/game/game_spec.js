const chai = require('chai');
const expect = chai.expect;

const responseExample = require('./state_response_example.json');
const Player = require('../../game/player.js');
const Resources = require('../../game/resources.js');
const Map = require('../../game/map.js');
const Virus = require('../../game/virus.js');
const Game = require('../../game/game.js');
const PlayerError = require('../../game/errors.js').UnknownPlayerIdException;
const Vector = require('victor');
const Utils = require('../../game/utils.js');

describe('Game', function() {
  it('parses correctly', function() {
    const game = Game.parse(responseExample.data, 1);

    expect(game).to.have.property('id', -1);
    expect(game).to.have.property('tick', 305);
    expect(game).to.have.property('timeLeft').to.be.within(1179.2318, 1179.232);
    expect(game).to.have.property('players').to.have.lengthOf(2);
    expect(game.players[0]).to.be.instanceof(Player);
    expect(game).to.have.property('resources').to.be.instanceof(Resources);
    expect(game).to.have.property('map').to.be.instanceof(Map);
    expect(game).to.have.property('viruses').to.have.lengthOf(15);
    expect(game.viruses[0]).to.be.instanceof(Virus);

    expect(game).to.have.property('me').to.be.instanceof(Player);
    expect(game.me).to.have.property('id', 1);

    expect(game).to.have.property('enemies').to.have.lengthOf(1);
    expect(game.enemies[0]).to.be.instanceof(Player);
  });

  it('throws if the player doesn\'t exist', function() {
    expect(Game.parse.bind(Game, responseExample.data, 42)).to.throw(PlayerError);
  });

  it('extracts actions correctly', function() {
    const game = Game.parse(responseExample.data, 1);
    game.me.cells[0].burst();
    game.me.cells[0].move(new Vector(42, 42));

    const actions = game.actions;
    expect(actions).to.have.lengthOf(1);
    expect(actions[0].export()).to.contain.all.keys({
      cell_id: 0,
      burst: true,
      split: false,
      trade: 0
    });
    expect(Utils.almostEqual(actions[0].target, new Vector(42, 42))).to.be.true;
  });
});
