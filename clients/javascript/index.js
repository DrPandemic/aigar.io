// Libraries
const argv = require('minimist')(process.argv.slice(2));
const sleep = require('sleep');

// Models
const Game = require('./game/game.js');
const API = require('./game/api.js');
const AI = require('./ai.js');
const gameLoop = require('./game/game_loop.js');

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

function readConfigFile(configFile) {
  const playerId = configFile.player_id;
  const playerSecret = configFile.player_secret;
  const apiUrl = configFile.api_url;

  if(playerId === defaultConfigValue ||
     playerSecret === defaultConfigValue ||
     apiUrl === defaultConfigValue) {
    throw errorChanged;
  }

  return {
    playerId,
    playerSecret,
    apiUrl
  };
}

function readArguments(params) {
  return Object.assign({}, params, {
    create: argv['create'] || false,
    join: argv['join'] || false
  });
}

function main(params) {
  let gameId = params.join ? params.playerId : Game.RANKED_GAME_ID;
  const api = new API(params.playerId, params.playerSecret, params.apiUrl);
  let ai = new AI();
  let previousTick = -1;

  if(params.create) {
    gameId = api.createPrivate();

    sleep.usleep(500000);
  }

  while(true) {
    const game = api.fetchGameState(gameId);

    if(game.tick < previousTick) {
      ai = AI();
    }
    previousTick = game.tick;

    gameLoop(api, gameId, ai.step);

    sleep.usleep(Math.floor(1000000 / Game.UPDATE_PER_SECOND));
  }
}

Promise.resolve(require(configFileName))
  .catch(() => {
    throw errorNotFound;
  })
  .then(readConfigFile)
  .then(readArguments)
  .then(main)
  .catch(error => console.error(error));
