const chai = require('chai');
const expect = chai.expect;

const responseExample = require('./state_response_example.json');
const Player = require('../../game/player.js');
const Cell = require('../../game/cell.js');

describe('Player', function() {
  it('parses correctly', function() {
    const p0 = Player.parse(responseExample.data.players[0]);

    expect(p0).to.have.property('id').to.equal(1);
    expect(p0).to.have.property('name').to.equal('player1');
    expect(p0).to.have.property('totalMass').to.equal(22);
    expect(p0).to.have.property('active').to.equal(false);
    expect(p0).to.have.property('cells').to.have.lengthOf(1);
    expect(p0.cells[0]).to.be.an.instanceof(Cell);
  });
});
