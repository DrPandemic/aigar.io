const chai = require('chai');
const expect = chai.expect;
const sinon = require('sinon');

const API = require('../../game/api.js');
const Game = require('../../game/game.js');
const responseExample = require('./state_response_example.json');
const Vector = require('victor');
const Utils = require('../../game/utils.js');

describe('API', function() {
  it('concatenate correctly urls', function() {
    const api0 = new API(0, 'foo', 'http://foo.bar/api/1', () => {});
    const api1 = new API(0, 'foo', 'http://foo.bar/api/1/', () => {});

    expect(api0).to.have.property('apiUrl').to.equal('http://foo.bar/api/1/game/');
    expect(api1).to.have.property('apiUrl').to.equal('http://foo.bar/api/1/game/');
  });

  describe('fetchGameState', function() {
    it('calls the appropriated route', function() {
      const stub = sinon.stub();
      stub.returns(Promise.resolve({ok: true, json: () => responseExample}));
      const api = new API(0, 'foo', 'http://foo.bar/api/1', stub);

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
      stub.returns(Promise.resolve({ok: true, json: () => responseExample}));
      const api = new API(0, 'foo', 'http://foo.bar/api/1', stub);

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
      const api = new API(0, 'foo', 'http://foo.bar/api/1', stub);

      return api.sendActions(1337, {})
        .then(() => {
          sinon.assert.calledWith(
            stub,
            'http://foo.bar/api/1/game/1337/action',
            sinon.match({method: 'post'})
          );
        });

    });

    it('passes the right body', function() {
      const stub = sinon.stub();
      stub.returns(Promise.resolve());
      const api = new API(0, 'foo', 'http://foo.bar/api/1', stub);

      const game = Game.parse(responseExample.data, 1);
      game.me.cells[0].target = new Vector(42, 42);

      return api.sendActions(1337, game.actions)
        .then(() => {
          sinon.assert.calledWith(
            stub,
            'http://foo.bar/api/1/game/1337/action',
            sinon.match(options => {
              const body = JSON.parse(options.body);

              const target = Vector.fromObject(body.actions[0].target);
              return body.player_secret === 'foo'
                && Utils.almostEqual(target, new Vector(42, 42))
                && expect(body.actions[0]).to.contain.all.keys({
                  cell_id: 0,
                  burst: true,
                  split: false,
                  trade: 0
                });
            })
          );
        });
    });
  });

  describe('createPrivate', function() {
    it('sends the request to the right url', function() {
      const stub = sinon.stub();
      stub.returns(Promise.resolve());
      const api = new API(0, 'foo', 'http://foo.bar/api/1', stub);

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
      const api = new API(0, 'foo', 'http://foo.bar/api/1', stub);

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
