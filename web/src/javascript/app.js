import {draw as drawGame, fetchState} from "./game";
import {drawLeaderboard} from "./gameLeaderboard";
import {createGameCanvas} from "./drawMap";

const gameCanvas = createGameCanvas();
let currentState;

async function updateLoop() {
  currentState = await fetchState(0);
  drawGame(currentState, gameCanvas);
}

async function updateLeaderBoard() {
  drawLeaderboard(currentState);
}

setInterval(updateLoop, 1000/24);
setInterval(updateLeaderBoard, 1000);
