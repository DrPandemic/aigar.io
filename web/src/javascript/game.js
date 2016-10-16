import {
  drawCellsOnMap,
  drawMap,
  drawMiniMap,
} from "./drawMap";

function extractPointsFromState(gameState) {
  return [].concat.apply(...gameState.players.map((p) => p.cells));
}

export function draw(gameState) {
  drawCellsOnMap(extractPointsFromState(gameState));
  drawMap();
  drawMiniMap();
}

export function update(gameState) {
}
