const epsilon = 0.02;
module.exports.epsilon = epsilon;

module.exports.almostEqual = function(v0, v1) {
  return v0.distanceSq(v1) < epsilon;
};
