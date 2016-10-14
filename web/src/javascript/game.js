import {
  drawFoodOnMap,
  drawMap,
  drawMiniMap,
  drawPlayersOnMap,
  initMap,
} from "./drawMap";

const apiURL = "http://localhost:1337/api/1/game/";

function extractPointsFromState(gameState) {
  return [].concat.apply(...gameState.players.map((p) => p.cells));
}

export function draw(gameState, canvas) {
  initMap(canvas);
  drawFoodOnMap(gameState.food, canvas);
  drawPlayersOnMap(gameState.players, canvas);
  drawMap(canvas);
  drawMiniMap(canvas);
}

export function update(gameState) {
  // To do a deep copy. It's seems to be the best solution
  const newState = JSON.parse(JSON.stringify(gameState));

  return newState;
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
