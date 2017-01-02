function UnknownPlayerIdError(message) {
  this.name = 'UnknownPlayerIdError';
  this.message = message || 'This player\'s id doesn\'t exist';
  this.stack = (new Error()).stack;
}
UnknownPlayerIdError.prototype = Object.create(Error.prototype);
UnknownPlayerIdError.prototype.constructor = UnknownPlayerIdError;


module.exports.UnknownPlayerIdException = UnknownPlayerIdError;
