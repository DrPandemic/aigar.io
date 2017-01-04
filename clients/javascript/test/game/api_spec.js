const chai = require('chai');
const expect = chai.expect;
const sinon = require('sinon');

const API = require('../../game/api.js');
const Game = require('../../game/game.js');
const responseExample = require('./state_response_example.json');

describe('API', function() {
  it('concatenate correctly urls', function() {
    const api0 = new API(0, 'foo', 'http://foo.bar', () => {});
    const api1 = new API(0, 'foo', 'http://foo.bar/', () => {});

    expect(api0).to.have.property('apiUrl').to.equal('http://foo.bar/api/1/game/');
    expect(api1).to.have.property('apiUrl').to.equal('http://foo.bar/api/1/game/');
  });

  describe('fetchGameState', function() {
    it('calls the appropriated route', function() {
      const stub = sinon.stub();
      stub.returns(Promise.resolve({json: () => responseExample.data}));
      const api = new API(0, 'foo', 'http://foo.bar', stub);

      return api.fetchGameState(1337, 1)
        .then(() => {
          sinon.assert.calledWith(
            stub,
            'http://foo.bar/api/1/game/1337',
            sinon.match({method: 'get'})
          );
        });
    });

    it('returns a Game object', function() {
      const stub = sinon.stub();
      stub.returns(Promise.resolve({json: () => responseExample.data}));
      const api = new API(0, 'foo', 'http://foo.bar', stub);

      return api.fetchGameState(1337, 1)
        .then(response => {
          expect(response).to.be.an.instanceof(Game);
        });
    });
  });

  describe('sendActions', function() {
    it('sends the actions to the right url', function() {
      const stub = sinon.stub();
      stub.returns(Promise.resolve());
      const api = new API(0, 'foo', 'http://foo.bar', stub);

      return api.sendActions(1337, {})
        .then(() => {
          sinon.assert.calledWith(
            stub,
            'http://foo.bar/api/1/game/1337/action',
            sinon.match({method: 'post'})
          );
        });

    });

    xit('passes the right body', function() {
      const stub = sinon.stub();
      stub.returns(Promise.resolve());
      const api = new API(0, 'foo', 'http://foo.bar', stub);

      return api.sendActions(1337, {})
        .then(() => {
          sinon.assert.calledWith(
            stub,
            'http://foo.bar/api/1/game/1337/action',
            sinon.match(options => {
              const body = JSON.parse(options.body);
              return body.player_secret === 'foo';
            })
          );
        });
    });
  });

  describe('createPrivate', function() {
    it('sends the request to the right url', function() {
      const stub = sinon.stub();
      stub.returns(Promise.resolve());
      const api = new API(0, 'foo', 'http://foo.bar', stub);

      return api.createPrivate()
        .then(() => {
          sinon.assert.calledWith(
            stub,
            'http://foo.bar/api/1/game/',
            sinon.match({method: 'post'})
          );
        });
    });

    it('passes the player\'s secret', function() {
      const stub = sinon.stub();
      stub.returns(Promise.resolve());
      const api = new API(0, 'foo', 'http://foo.bar', stub);

      return api.createPrivate()
        .then(() => {
          sinon.assert.calledWith(
            stub,
            'http://foo.bar/api/1/game/',
            sinon.match(options => {
              const body = JSON.parse(options.body);
              return body.player_secret === 'foo';
            })
          );
        });
    });
  });
});

