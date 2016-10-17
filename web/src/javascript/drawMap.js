import $ from "jquery";
import * as constants from "./constants";

const screenCanvas = $("#screenCanvas")[0];
const screenContext = screenCanvas.getContext("2d");
const screenWidth = screenCanvas.width;
const screenHeight = screenCanvas.height;
//Static position for tests for the screen window on the mini-map
var xScreenPosOnMap = 200;
var yScreenPosOnMap = 200;

export const mapWidth = screenWidth*3;
export const mapHeight = screenHeight*3;

const miniMapCanvas = document.createElement("canvas");
const miniMapContext = miniMapCanvas.getContext("2d");
const miniMapWidth = screenWidth/4;
const miniMapHeight = screenHeight/4;
const miniMapPosX = screenWidth-miniMapWidth;

const miniMapScreenPosWidth = miniMapWidth/3;
const miniMapScreenPosHeight = miniMapHeight/3;

export function createGameCanvas() {
  return document.createElement("canvas");
}

export function initMap(canvas) {
  canvas.width = mapWidth;
  canvas.height = mapHeight;
}

export function getPlayerColor(players, currentPlayer) {
  const playerPosition = players
    .sort((a, b) => a.id - b.id)
    .findIndex(player => player.id === currentPlayer.id);
  return constants.playerColors[playerPosition];
}

export function drawPlayersOnMap(players, canvas) {
  const context = canvas.getContext("2d");
  for(const player of players) {
    const color = getPlayerColor(players, player);
    for(const cell of player.cells) {
      context.beginPath();
      context.arc(cell.position.x, cell.position.y, cell.mass, 0, Math.PI * 2, false);
      context.fillStyle = color;
      context.fill();
    }
  }
}

export function drawFoodOnMap(foods, canvas) {
  const context = canvas.getContext("2d");
  const drawFood = (foods, color) => {
    for(const food of foods) {
      context.beginPath();
      context.arc(food.x, food.y, constants.foodMass, 0, Math.PI * 2, false);
      context.fillStyle = color;
      context.fill();
    }
  };

  drawFood(foods.regular, constants.regularColor);
  drawFood(foods.silver, constants.silverColor);
  drawFood(foods.gold, constants.goldColor);
}

export function drawMap(canvas) {
  screenContext.clearRect(0, 0, screenWidth, screenHeight);
  screenContext.drawImage(canvas, xScreenPosOnMap, yScreenPosOnMap, screenWidth, screenHeight, 0, 0, screenWidth, screenHeight);
}

export function drawMiniMap(canvas) {
  miniMapContext.clearRect(0, 0, screenWidth, screenHeight);

  //set dimensions
  miniMapCanvas.width = screenWidth;
  miniMapCanvas.height = screenHeight;

  //MiniMap background
  miniMapContext.rect(0,0,miniMapWidth,miniMapHeight);
  miniMapContext.fillStyle = "rgba(58, 58, 58, 0.85)";
  miniMapContext.fill();

  //apply the old canvas to the new one
  miniMapContext.drawImage(canvas, 0, 0, miniMapWidth, miniMapHeight);

  drawMiniMapminiMapScreenPos();

  screenContext.drawImage(miniMapCanvas, miniMapPosX,0);
  screenCanvas.style.background = "#000";
}

function drawMiniMapminiMapScreenPos(){
  miniMapContext.strokeStyle="#fff";
  var xMiniMapPos = miniMapWidth/mapWidth*xScreenPosOnMap;
  var yMiniMapPos = miniMapHeight/mapHeight*yScreenPosOnMap;
  miniMapContext.strokeRect(xMiniMapPos,yMiniMapPos,miniMapScreenPosWidth,miniMapScreenPosHeight);
}
