import fakeState from "./fakeState";
import {draw as drawGame} from "./game";

import {createGameCanvas} from "./drawMap";

const gameCanvas = createGameCanvas();

setInterval(function() {
  drawGame(fakeState.data, gameCanvas);
}, 1000/24);
