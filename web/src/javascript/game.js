import {
  drawCellsOnMap,
  drawFoodOnMap,
  drawMap,
  drawMiniMap,
  initMap,
} from "./drawMap";

function extractCellsFromState(gameState) {
  return [].concat.apply(...gameState.players.map((p) => p.cells));
}

export function draw(gameState, canvas) {
  initMap(canvas);
  drawFoodOnMap(gameState.food, canvas);
  drawCellsOnMap(extractCellsFromState(gameState), canvas);
  drawMap(canvas);
  drawMiniMap(canvas);
}
