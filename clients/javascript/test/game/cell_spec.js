const chai = require('chai');
const chaiAsPromised = require('chai-as-promised');
chai.use(chaiAsPromised);
const expect = chai.expect;

const responseExample = require('./state_response_example.json');
const Cell = require('../../game/cell.js');
const Utils = require('../../game/utils.js');
const Vector = require('victor');

describe('Cell', function() {
  it('parses correctly', function() {
    const c0 = Cell.parse(responseExample.data.players[0].cells[0]);

    expect(c0).to.have.property('id').to.equal(0);
    expect(c0).to.have.property('mass').to.equal(22);
    expect(c0).to.have.property('radius').to.equal(18);
    expect(c0).to.have.property('position').to.be.instanceof(Vector);
    expect(Utils.almostEqual(c0.position, new Vector(924.8320, 351.1990))).to.be.true;
    expect(c0).to.have.property('target').to.be.instanceof(Vector);
    expect(Utils.almostEqual(c0.target, new Vector(590.6243, 305.7435))).to.be.true;
  });

  it('actions() reset the actions', function() {
    const c0 = Cell.parse(responseExample.data.players[0].cells[0]);
    c0.burst();

    expect(c0.actions()).to.have.property('burst').to.be.true;
    expect(c0.actions()).to.have.property('burst').to.be.false;
  });

  it('can burst', function() {
    const c0 = Cell.parse(responseExample.data.players[0].cells[0]);
    c0.burst();

    expect(c0.actions()).to.have.property('burst').to.be.true;
  });

  it('can move', function() {
    const c0 = Cell.parse(responseExample.data.players[0].cells[0]);
    c0.move(new Vector(42, 42));

    expect(Utils.almostEqual(c0.target, new Vector(42, 42))).to.be.true;
    expect(Utils.almostEqual(c0.actions().target, new Vector(42, 42))).to.be.true;
  });

  it('can split', function() {
    const c0 = Cell.parse(responseExample.data.players[0].cells[0]);
    c0.split();

    expect(c0.actions()).to.have.property('split').to.be.true;
  });

  it('can trade', function() {
    const c0 = Cell.parse(responseExample.data.players[0].cells[0]);
    c0.trade(42);

    expect(c0.actions()).to.have.property('trade').to.be.equal(42);
  });
});
