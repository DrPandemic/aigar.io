const chai = require('chai');
const chaiAsPromised = require('chai-as-promised');
chai.use(chaiAsPromised);
const expect = chai.expect;

const responseExample = require('./state_response_example.json');
const Resources = require('../../game/resources.js');
const Utils = require('../../game/utils.js');
const Vector = require('victor');

describe('Resources', function() {
  it('parses correctly', function() {
    const resources = Resources.parse(responseExample.data.resources);

    expect(resources).to.have.property('regular').of.lengthOf(11);
    expect(resources).to.have.property('silver').of.lengthOf(7);
    expect(resources).to.have.property('gold').of.lengthOf(3);

    expect(Utils.almostEqual(resources.regular[0], new Vector(1451.0559, 14.9614))).to.be.true;
  });
});
