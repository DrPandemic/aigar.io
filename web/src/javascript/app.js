import {drawLeaderboard} from "./gameLeaderboard";
import {drawGame, createGameCanvas, interpolateState} from "./game";
import {gameRefresh, leaderboardRefresh, gameDelay} from "./constants";

const gameCanvas = createGameCanvas();
let gameRunning = false;
let leaderboardRunning = false;

const states = [];

const networkWorker = new Worker("javascript/gameWebWorker.bundle.js");
networkWorker.onmessage = message => {
  // This is to prevent Chrome's GC from deleting the worker.
  // It's happening on Chrome but not on FF.
  if(!networkWorker) {
    console.error("Got GCed");
  }
  states.push({
    ...message.data,
    timestamp: (new Date()).getTime(),
  });

  triggerStart(states);
};

function triggerStart() {
  if(!testStates()) return;

  if(!gameRunning) updateGame();
  if(!leaderboardRunning) updateLeaderBoard();
}

function testStates() {
  return (states.length >= 2) &&
    (states[0].timestamp < ((new Date()).getTime()) - gameDelay);
}

async function updateGame() {
  const startTime = (new Date()).getTime();
  gameRunning = false;
  if(!testStates()) return;
  gameRunning = true;

  const prev = states[0];
  const next = states[1];
  const ratio = (startTime - gameDelay - prev.timestamp) / (next.timestamp - prev.timestamp);

  const currentState = interpolateState(prev, next, ratio);
  if(currentState.tick === next.tick) states.shift();
  drawGame(currentState, gameCanvas);

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(updateGame, 1000/gameRefresh - elapsed);
}

function updateLeaderBoard() {
  const startTime = (new Date()).getTime();
  leaderboardRunning = false;
  if(!testStates()) return;
  leaderboardRunning = true;

  drawLeaderboard(states[0]);

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(updateLeaderBoard, 1000/leaderboardRefresh - elapsed);
}

updateGame();
updateLeaderBoard();
