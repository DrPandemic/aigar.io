import {drawLeaderboard} from "./gameLeaderboard";
import {drawGame, createGameCanvas} from "./game";

const gameCanvas = createGameCanvas();
let currentState;

(new Worker("javascript/gameWebWorker.bundle.js")).onmessage = message => {
  currentState = message;
};

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

setInterval(updateLoop, 1000/24);
setInterval(updateLeaderBoard, 1000);
