import fakeState from "./fakeState";
import {draw as drawGame, update as updateGame} from "./game";

setInterval(function() {
  //updateGame(fakeState);
  drawGame(fakeState.data);
}, 1000/24);
