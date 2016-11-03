import {drawLeaderboard} from "./gameLeaderboard";
import {drawGame, createGameCanvas} from "./game";
import {gameRefresh, leaderboardRefresh} from "./constants";

const gameCanvas = createGameCanvas();
let currentState;

(new Worker("javascript/gameWebWorker.bundle.js")).onmessage = message => currentState = message.data;

async function updateLoop() {
  if(currentState) {
    drawGame(currentState, gameCanvas);
  }
}

function updateLeaderBoard() {
  if(currentState) {
    drawLeaderboard(currentState);
  }
}

setInterval(updateLoop, 1000/gameRefresh);
setInterval(updateLeaderBoard, 1000/leaderboardRefresh);
