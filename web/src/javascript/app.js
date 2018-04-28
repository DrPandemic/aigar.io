import {drawLeaderboard} from "./gameLeaderboard";
import {drawGame, interpolateState, initCanvas, prepareCanvases} from "./game";
import {
  debug,
  gameDelay,
  gameRefresh,
  leaderboardRefresh,
  maximumStoredStates,
  rankedGameId,
} from "./constants";
import {
  initLineButton,
  createCanvas,
  displayLoading,
  hideLoading,
  displayDoesntExist,
  getCurrentGameId,
} from "./gameUI";

let gameLoadingHandle = displayLoading();

const gameCanvas = createCanvas();
const miniMapCanvas = createCanvas();
const miniMapTmpCanvas = createCanvas();
let gameRunning = false;
let leaderboardRunning = false;

const states = [];

const networkWorker = new Worker("javascript/gameWebWorker.bundle.js");
const gameId = getCurrentGameId();
networkWorker.postMessage(gameId === null ? rankedGameId : gameId);
networkWorker.onmessage = message => {
  if(!message.data) {
    hideLoading(gameLoadingHandle);
    gameRunning = false;
    leaderboardRunning = false;
    displayDoesntExist();
    return;
  }

  states.push({
    ...message.data,
    timestamp: new Date().getTime(),
  });

  if(states.length > maximumStoredStates) {
    states.shift();
  }

  triggerStart(states);
};

// This is to prevent Chrome's GC from deleting the worker.
// It's happening on Chrome but not on FF.
setTimeout(() => networkWorker, 1000);

function triggerStart() {
  if(!canInterpolateStates()) {
    return;
  }

  // Initiate the update loops for the game and leaderboard
  if(!gameRunning) {
    gameRunning = true;
    if(gameLoadingHandle) {
      hideLoading(gameLoadingHandle);
      gameLoadingHandle = undefined;
    }

    updateGame(getGameState());
  }
  if(!leaderboardRunning) {
    leaderboardRunning = true;
    updateLeaderboard();
  }
}

function canInterpolateStates() {
  return (states.length >= 2) &&
    states[0].timestamp < new Date().getTime() - gameDelay;
}

function updateGame(previousState) {
  if(!gameRunning) {
    return;
  }
  try {
    const startTime = (new Date()).getTime();
    drawGame(previousState, gameCanvas, miniMapCanvas);

    const currentState = getGameState(startTime);

    gameRunning = false;
    if(!canInterpolateStates()) {
      return;
    }
    gameRunning = true;

    prepareCanvases(currentState, gameCanvas, miniMapCanvas, miniMapTmpCanvas);

    const elapsed = new Date().getTime() - startTime;
    setTimeout(() => updateGame(currentState), 1000 / gameRefresh - elapsed);
  } catch(error) {
    gameRunning = false;
    if(debug) {
      console.error(error);
    }
  }
}

function getGameState(startTime = new Date().getTime()) {
  const prev = states[0];
  const next = states[1];
  const ratio = (startTime - gameDelay - prev.timestamp) / (next.timestamp - prev.timestamp);

  const currentState = interpolateState(prev, next, ratio);
  if(currentState.tick === next.tick) {
    states.shift();
  }

  return currentState;
}

function updateLeaderboard() {
  if(!leaderboardRunning) {
    return;
  }
  try {
    const startTime = (new Date()).getTime();

    leaderboardRunning = false;
    if(!canInterpolateStates()) {
      return;
    }
    leaderboardRunning = true;

    drawLeaderboard(states[0]);

    const elapsed = new Date().getTime() - startTime;
    setTimeout(updateLeaderboard, 1000 / leaderboardRefresh - elapsed);
  } catch(error) {
    leaderboardRunning = false;
    if(debug) {
      console.error(error);
    }
  }
}

function initWebSocket() {
  const request = new atmosphere.AtmosphereRequest();
  request.url = `${window.location.origin}/websocket/1/`;
  request.contentType = "application/json";
  request.transport = "websocket";
  request.fallbackTransport = "long-polling";

  request.onOpen = function(response) {
    console.log("onOpen", response);
  };

  request.onReconnect = function (request, response) {
    console.log("onReconnect", response);
  };

  request.onMessage = function (response) {
    const message = response.responseBody;
    let json;
    try {
      json = JSON.parse(message);
    } catch (e) {
      console.log("Error: ", message.data);
      return;
    }
    console.log("onMessage", json);
  };

  request.onError = function(response) {
    console.log("onError", response);
  };
  const subSocket = atmosphere.subscribe(request);
  subSocket.push("some data");
}

initCanvas();
initLineButton();
initWebSocket();
