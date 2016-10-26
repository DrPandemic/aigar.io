import $ from "jquery";
import * as constants from "./constants";
import sort from "immutable-sort";

const screenCanvas = $("#screenCanvas")[0];
const screenContext = screenCanvas.getContext("2d");
const screenWidth = screenCanvas.width;
const screenHeight = screenCanvas.height;
//Static position for tests for the screen window on the mini-map

let xScreenPosOnMap = 0;
let yScreenPosOnMap = 0;

let screenToMapRatioWidth;
let screenToMapRatioHeight;

const miniMapCanvas = document.createElement("canvas");
const miniMapContext = miniMapCanvas.getContext("2d");
const miniMapWidth = screenWidth / 4;
let miniMapHeight;
const miniMapPosX = screenWidth - miniMapWidth;

let miniMapScreenPosWidth;
let miniMapScreenPosHeight;

function drawCircle(context, position, radius, color) {
  context.beginPath();
  context.arc(position.x, position.y, radius, 0, Math.PI * 2, false);
  context.fillStyle = color;
  context.fill();
}

export function createGameCanvas() {
  return document.createElement("canvas");
}

export function initMap(canvas, map) {
  canvas.width = map.width;
  canvas.height = map.height;
  
  screenToMapRatioWidth = canvas.width/ screenCanvas.width;
  screenToMapRatioHeight = canvas.height/ screenCanvas.height;
  miniMapScreenPosWidth = miniMapWidth/screenToMapRatioWidth;
  miniMapScreenPosHeight = miniMapHeight/screenToMapRatioHeight;
}

export function getPlayerColor(players, currentPlayer) {
  const playerPosition = sort(players, (a, b) => a.id - b.id)
    .findIndex(player => player.id === currentPlayer.id);
  return constants.playerColors[playerPosition];
}

export function drawPlayersOnMap(players, canvas) {
  const context = canvas.getContext("2d");
  for(const player of players) {
    const color = getPlayerColor(players, player);
    for(const cell of player.cells) {
      drawCircle(context, cell.position, cell.mass, color);
    }
  }
}

export function drawFoodOnMap(foods, canvas) {
  const context = canvas.getContext("2d");
  const drawFood = (foods, color, rgba, mass) => {
    for(const food of foods) {
      var grd=context.createRadialGradient(food.x,food.y, .5, food.x, food.y,constants.foodMass);
      grd.addColorStop(0,color);
      grd.addColorStop(1, rgba);
      drawCircle(context, food, mass, grd);
    }
  };
  
  drawFood(foods.regular, constants.regularColor, constants.regularRGBColor, constants.regFoodMass);
  drawFood(foods.silver, constants.silverColor, constants.silverRGBColor, constants.foodMass);
  drawFood(foods.gold, constants.goldColor, constants.goldRGBColor,  constants.foodMass);
}

export function drawMap(canvas) {
  screenContext.clearRect(0, 0, screenWidth, screenHeight);
  screenContext.drawImage(canvas, xScreenPosOnMap, yScreenPosOnMap, screenWidth, screenHeight, 0, 0, screenWidth, screenHeight);
}

export function initMiniMap(canvas) {
  miniMapContext.clearRect(0, 0, screenWidth, screenHeight);
  miniMapHeight = miniMapWidth*canvas.height/canvas.width;

  //set dimensions
  miniMapCanvas.width = screenWidth;
  miniMapCanvas.height = screenHeight;

  //MiniMap background
  miniMapContext.rect(0, 0, miniMapWidth, miniMapHeight);
  miniMapContext.fillStyle = "rgba(58, 58, 58, 0.85)";
  miniMapContext.fill();

  //apply the old canvas to the new one
  miniMapContext.drawImage(canvas, 0, 0, miniMapWidth, miniMapHeight);
}

export function drawMiniMap(canvas) {
  drawMiniMapScreenPos(canvas);
  screenContext.drawImage(miniMapCanvas, miniMapPosX, 0);
  screenCanvas.style.background = "#000";
}

function drawMiniMapScreenPos(canvas) {
  miniMapContext.strokeStyle = "#fff";
  const xMiniMapPos = miniMapWidth / canvas.width * xScreenPosOnMap;
  const yMiniMapPos = miniMapHeight / canvas.height * yScreenPosOnMap;
  miniMapContext.strokeRect(xMiniMapPos, yMiniMapPos, miniMapScreenPosWidth, miniMapScreenPosHeight);
}

function changeScreenPos(mousePos) {
  let miniMapPos = {
    x : (mousePos.x - miniMapPosX) - (miniMapScreenPosWidth / 2),
    y : mousePos.y - (miniMapScreenPosHeight / 2)
  };
  miniMapPos = keepInsideMap(miniMapPos);

  xScreenPosOnMap = miniMapPos.x * screenToMapRatioWidth * (screenWidth/miniMapWidth);
  yScreenPosOnMap = miniMapPos.y * screenToMapRatioHeight * (screenHeight/miniMapHeight);
}

function keepInsideMap(pos) {
  if (pos.x < 0) {
    pos.x = 0;
  } else if (pos.x > miniMapWidth - miniMapScreenPosWidth) {
    pos.x = miniMapWidth - miniMapScreenPosWidth;
  }
  if (pos.y < 0) {
    pos.y = 0;
  } else if (pos.y > miniMapHeight - miniMapScreenPosHeight) {
    pos.y = miniMapHeight - miniMapScreenPosHeight;
  }
  return pos;
}

function getMousePos(evt) {
  const rect = screenCanvas.getBoundingClientRect();
  return {
    x : (evt.clientX - rect.left) / (rect.right - rect.left) * screenCanvas.width,
    y : (evt.clientY - rect.top) / (rect.bottom - rect.top) * screenCanvas.height
  };
}

screenCanvas.addEventListener("click", function (evt) {
  const mousePos = getMousePos(evt);
  if (mousePos.x > miniMapPosX && mousePos.y < miniMapHeight) {
    changeScreenPos(mousePos);
  }
}, false);
