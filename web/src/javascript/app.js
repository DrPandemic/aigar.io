import {drawLeaderboard} from "./gameLeaderboard";
import {drawGame, createGameCanvas} from "./game";
import {gameRefresh, leaderboardRefresh} from "./constants";

const gameCanvas = createGameCanvas();

let currentState;
const networkWorker = new Worker("javascript/gameWebWorker.bundle.js");
networkWorker.onmessage = message => {
  // This is to prevent Chrome's GC from deleting the worker.
  // It's happening on Chrome but not on FF.
  if(!networkWorker) {
    console.error("Got GCed");
  }
  currentState = message.data;
};

async function updateGame() {
  const startTime = (new Date()).getTime();
  if(currentState) {
    drawGame(currentState, gameCanvas);
  }

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(updateGame, 1000/gameRefresh - elapsed);
}

function updateLeaderBoard() {
  const startTime = (new Date()).getTime();
  if(currentState) {
    drawLeaderboard(currentState);
  }

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(updateLeaderBoard, 1000/leaderboardRefresh - elapsed);
}

updateGame();
updateLeaderBoard();
