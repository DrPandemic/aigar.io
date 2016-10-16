import fakeState from "./fakeState";
import {draw as drawGame} from "./game";
import {drawLeaderboard} from "./gameLeaderboard";
import {createGameCanvas} from "./drawMap";

const gameCanvas = createGameCanvas();

function updateLoop() {
  drawGame(fakeState.data, gameCanvas);
  drawLeaderboard(fakeState.data);
}

setInterval(updateLoop, 1000/24);
