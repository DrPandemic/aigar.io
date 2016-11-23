import {drawLeaderboard} from "./gameLeaderboard";
import {drawGame, createCanvas, interpolateState, initCanvas} from "./game";
import {gameRefresh, leaderboardRefresh, gameDelay, maximumStoredStates} from "./constants";
import {initLineButton} from "./gameInit";

const gameCanvas = createCanvas();
const miniMapCanvas = createCanvas();
const miniMapTmpCanvas = createCanvas();
let gameRunning = false;
let leaderboardRunning = false;

const states = [];

const networkWorker = new Worker("javascript/gameWebWorker.bundle.js");
networkWorker.onmessage = message => {
  states.push({
    ...message.data,
    timestamp: (new Date()).getTime(),
  });

  if(states.length > maximumStoredStates) {
    states.shift();
  }

  triggerStart(states);
};

// This is to prevent Chrome's GC from deleting the worker.
// It's happening on Chrome but not on FF.
setTimeout(() => networkWorker,1000);

function triggerStart() {
  if(!canInterpolateStates()) return;

  // Initiate the update loops for the game and leaderboard
  if(!gameRunning) updateGame();
  if(!leaderboardRunning) updateLeaderBoard();
}

function canInterpolateStates() {
  return (states.length >= 2) &&
    states[0].timestamp < new Date().getTime() - gameDelay;
}

function updateGame() {
  const startTime = (new Date()).getTime();
  gameRunning = false;
  if(!canInterpolateStates()) return;
  gameRunning = true;

  const prev = states[0];
  const next = states[1];
  const ratio = (startTime - gameDelay - prev.timestamp) / (next.timestamp - prev.timestamp);

  const currentState = interpolateState(prev, next, ratio);
  if(currentState.tick === next.tick) states.shift();
  drawGame(currentState, gameCanvas, miniMapCanvas, miniMapTmpCanvas);

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(updateGame, 1000/gameRefresh - elapsed);
}

function updateLeaderBoard() {
  const startTime = (new Date()).getTime();
  leaderboardRunning = false;
  if(!canInterpolateStates()) return;
  leaderboardRunning = true;

  drawLeaderboard(states[0]);

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(updateLeaderBoard, 1000/leaderboardRefresh - elapsed);
}

initCanvas();
initLineButton();
updateGame();
updateLeaderBoard();
