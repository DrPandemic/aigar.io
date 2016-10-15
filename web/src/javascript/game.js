import {
  drawFoodOnMap,
  drawMap,
  drawMiniMap,
  drawPlayersOnMap,
  initMap,
} from "./drawMap";

export function draw(gameState, canvas) {
  initMap(canvas);
  drawFoodOnMap(gameState.food, canvas);
  drawPlayersOnMap(gameState.players, canvas);
  drawMap(canvas);
  drawMiniMap(canvas);
}
