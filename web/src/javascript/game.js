import {
  drawResourcesOnMap,
  drawMap,
  initMiniMap,
  drawMiniMap,
  drawPlayersOnMap,
  initMap,
} from "./drawMap";

const apiURL = "http://localhost:1337/api/1/game/";

export function draw(gameState, canvas) {
  initMap(canvas, gameState.map);
  drawPlayersOnMap(gameState.players, canvas);
  initMiniMap(canvas);
  drawResourcesOnMap(gameState.resources, canvas);
  drawMap(canvas);
  drawMiniMap(canvas);
}

export function fetchState(gameId) {
  return fetch(`${apiURL}${gameId}`, {method: "get"})
    .then(response => {
      return response.json();
    })
    .then(response => {
      return response.data;
    });
}
