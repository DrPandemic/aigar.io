const chai = require('chai');
const expect = chai.expect;

const responseExample = require('./state_response_example.json');
const Map = require('../../game/map.js');

describe('Map', function() {
  it('parses correctly', function() {
    const resources = Map.parse(responseExample.data.map);

    expect(resources).to.have.property('width').to.be.equal(1500);
    expect(resources).to.have.property('height').to.be.equal(1500);
  });
});
