const chai = require('chai');
const expect = chai.expect;

const CellAction = require('../../game/cell-action.js');
const Vector = require('victor');
const Utils = require('../../game/utils.js');

describe('CellAction', function() {
  it('exporting an empty action returns null', function() {
    const action = new CellAction();

    expect(action.export()).to.be.null;
  });

  it('exports an action', function() {
    const action = new CellAction(0);
    action.burst = true;
    action.target = new Vector(10, 10);

    const ex = action.export();

    expect(ex).to.contain.all.keys({
      cell_id: 0,
      burst: true,
      split: false,
      trade: 0
    });
    expect(Utils.almostEqual(ex.target, new Vector(10, 10)));
  });
});
