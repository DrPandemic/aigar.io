module.exports = class CellAction {
  constructor(cellId) {
    this.changed_ = false;
    this.cellId_ = cellId;
    this.target_ = undefined;
    this.burst_ = false;
    this.split_ = false;
    this.trade_ = 0;
  }

  set cellId(id) {
    this.cellId_ = id;
    this.changed = true;
  }
  get cellId() {return this.cellId_;}

  set target(t) {
    this.target_ = t;
    this.changed = true;
  }
  get target() {return this.target_;}

  set burst(b) {
    this.burst_ = b;
    this.changed = true;
  }
  get burst() {return this.burst_;}

  set split(s) {
    this.split_ = s;
    this.changed = true;
  }
  get split() {return this.split_;}

  set trade(t) {
    this.trade_ = t;
    this.changed = true;
  }
  get trade() {return this.trade_;}

  export() {
    if(!this.changed) {
      return null;
    }

    return {
      cell_id: this.cellId,
      target: this.target,
      burst: this.burst,
      split: this.split,
      trade: this.trade
    };
  }
};
