import {drawLeaderboard} from "./gameLeaderboard";
import {drawGame, createGameCanvas, updateState} from "./game";
import {gameRefresh, leaderboardRefresh} from "./constants";

const gameCanvas = createGameCanvas();

let lastState;
let nextState;
const nextStates = [];
let lastTick = (new Date()).valueOf();

const networkWorker = new Worker("javascript/gameWebWorker.bundle.js");
networkWorker.onmessage = message => {
  // This is to prevent Chrome's GC from deleting the worker.
  // It's happening on Chrome but not on FF.
  if(!networkWorker) {
    console.error("Got GCed");
  }
  nextStates.push({
    ...message.data,
    timestamp: (new Date()).valueOf(),
  });
};

async function updateGame() {
  const startTime = (new Date()).getTime();

  const currentState = updateState(prev, next, diff);
  drawGame(currentState, gameCanvas);

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(updateGame, 1000/gameRefresh - elapsed);
}

function updateLeaderBoard() {
  const startTime = (new Date()).getTime();
  if(lastState) {
    drawLeaderboard(lastState);
  }

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(updateLeaderBoard, 1000/leaderboardRefresh - elapsed);
}

updateGame();
updateLeaderBoard();
