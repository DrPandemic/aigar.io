const chai = require('chai');
const chaiAsPromised = require('chai-as-promised');
chai.use(chaiAsPromised);
const expect = chai.expect;

const CellAction = require('../../game/cell-action.js');

describe('CellAction', function() {
  it('exporting an empty action returns null', function() {
    const action = new CellAction();

    expect(action.export()).to.be.null;
  });

  it('exports an action', function() {
    const action = new CellAction();
    action.burst = true;

    expect(action.export()).to.contain.all.keys({
      cell_id: 0,
      target: null,
      burst: true,
      split: false,
      trade: 0
    });
  });
});
