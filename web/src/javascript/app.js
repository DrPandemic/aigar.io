import {drawLeaderboard} from "./gameLeaderboard";
import {
  drawGame,
  interpolateState,
  initCanvas,
  prepareCanvases,
  initState,
} from "./game";
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
  displayLoading,
  hideLoading,
  displayDoesntExist,
  getCurrentGameId,
  updateInformationHeader,
} from "./gameUI";

let gameLoadingHandle = displayLoading();

const state = initState();
let gameRunning = false;
let leaderboardRunning = false;

const gameStates = [];

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

  gameStates.push({
    ...message.data,
    timestamp: new Date().getTime(),
  });

  if(gameStates.length > maximumStoredStates) {
    gameStates.shift();
  }

  triggerStart(state);
};

// This is to prevent Chrome's GC from deleting the worker.
// It's happening on Chrome but not on FF.
setTimeout(() => networkWorker, 1000);

function triggerStart(state) {
  updateInformationHeader(gameStates[0]);

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

    state.game = getGameState();
    updateGame(state);
  }
  if(!leaderboardRunning) {
    leaderboardRunning = true;
    updateLeaderboard(state);
  }
}

function canInterpolateStates() {
  return (gameStates.length >= 2) &&
    gameStates[0].timestamp < new Date().getTime() - gameDelay;
}

function updateGame(state) {
  if(!gameRunning) {
    return;
  }
  try {
    const startTime = (new Date()).getTime();
    drawGame(state);

    state.game = getGameState(startTime);
    state.display.canvasWidth = state.game.map.width;
    state.display.canvasHeight = state.game.map.height;


    gameRunning = false;
    if(!canInterpolateStates()) {
      return;
    }
    gameRunning = true;

    prepareCanvases(state);

    const elapsed = new Date().getTime() - startTime;
    setTimeout(() => updateGame(state), 1000 / gameRefresh - elapsed);
  } catch(error) {
    gameRunning = false;
    if(debug) {
      console.error(error);
    }
  }
}

function getGameState(startTime = new Date().getTime()) {
  const prev = gameStates[0];
  const next = gameStates[1];
  const ratio = (startTime - gameDelay - prev.timestamp) / (next.timestamp - prev.timestamp);

  const currentState = interpolateState(prev, next, ratio);
  if(currentState.tick === next.tick) {
    gameStates.shift();
  }

  return currentState;
}

function updateLeaderboard(state) {
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

    drawLeaderboard(state, gameStates[0]);

    const elapsed = new Date().getTime() - startTime;
    setTimeout(() => updateLeaderboard(state), 1000 / leaderboardRefresh - elapsed);
  } catch(error) {
    leaderboardRunning = false;
    if(debug) {
      console.error(error);
    }
  }
}

initCanvas(state);
initLineButton();
