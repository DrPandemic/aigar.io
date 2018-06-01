process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';

// Libraries
const argv = require('minimist')(process.argv.slice(2));
const sleep = require('sleep');

// Models
const Game = require('./game/game.js');
const API = require('./game/api.js');
const AI = require('./ai.js');

// Configs
const configFileName = './player.json';
const defaultConfigFileName = 'player.default.json';
const defaultConfigValue = 'REPLACEME';

// Errors
const errorChanged = `
WARNING: Did you forget to change your player
id/secret/api_url in '${configFileName}'? `;
const errorNotFound = `
ERROR: Could not find '${configFileName}'.
Did you forget to rename '${defaultConfigFileName}' to '${configFileName}'? `;

function readConfigFile(c) {
  const id = parseInt(c.player_id);

  if(id === defaultConfigValue ||
     c.player_secret === defaultConfigValue ||
     c.api_url === defaultConfigValue) {
    throw errorChanged;
  }

  return {
    playerId: id,
    playerSecret: c.player_secret,
    apiUrl: c.api_url
  };
}

function readArguments(params) {
  return Object.assign({}, params, {
    create: argv['create'] || false,
    join: argv['join'] || false
  });
}

async function loop(gameId, playerId, api, ai, previousTick) {
  try {
    let game = await api.fetchGameState(gameId, playerId);
    if(game.tick < previousTick) {
      ai = new AI();
    }
    game = ai.step(game);
    await api.sendActions(game.id, game.actions);

    setTimeout(() => loop(gameId, playerId, api, ai, game.tick), 1000 / Game.UPDATE_PER_SECOND);
  } catch(error) {
    console.error(error);
    setTimeout(() => loop(gameId, playerId, api, ai, previousTick), 5000);
  }
}

async function main(params) {
  let gameId = params.join ? params.playerId : Game.RANKED_GAME_ID;
  const api = new API(params.playerId, params.playerSecret, params.apiUrl);
  let ai = new AI();

  if(params.create) {
    try {
      gameId = await api.createPrivate();
    } catch(e) {
      console.error(`You received an error ${e.message}`);
    }
    sleep.usleep(50 * 1000);
  }

  return loop(gameId, params.playerId, api, ai, -1);
}

Promise.resolve(require(configFileName))
  .catch(() => {
    throw errorNotFound;
  })
  .then(readConfigFile)
  .then(readArguments)
  .then(main)
  .catch(error => console.error(error));
