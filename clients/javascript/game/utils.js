const Vector = require('victor');

const epsilon = 0.02;
module.exports.epsilon = epsilon;

module.exports.almostEqual = function(v0, v1) {
  if(!(v0 instanceof Vector)) {
    v0 = Vector.fromObject(v0);
  }
  if(!(v1 instanceof Vector)) {
    v1 = Vector.fromObject(v1);
  }

  return v0.distanceSq(v1) < epsilon;
};
