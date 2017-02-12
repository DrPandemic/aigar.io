const chai = require('chai');
const chaiAsPromised = require('chai-as-promised');
chai.use(chaiAsPromised);
const expect = chai.expect;

const Cell = require('../game/cell.js');

describe('Cell', () => {
  it('should contain its position', () => {
    const cell = new Cell(1);

    expect(cell).to.have.property('position').to.equal(1);
  });
});
