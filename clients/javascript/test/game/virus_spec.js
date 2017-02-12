const chai = require('chai');
const expect = chai.expect;

const responseExample = require('./state_response_example.json');
const Virus = require('../../game/virus.js');
const Utils = require('../../game/utils.js');
const Vector = require('victor');

describe('Virus', function() {
  it('parses correctly', function() {
    const virus = Virus.parse(responseExample.data.viruses[0]);

    expect(virus).to.have.property('mass').to.be.equal(100);
    expect(virus).to.have.property('radius').to.be.equal(34);
    expect(virus).to.have.property('position').to.be.instanceof(Vector);
    expect(Utils.almostEqual(virus.position, new Vector(886.2067, 1474.7122))).to.be.true;
  });
});
