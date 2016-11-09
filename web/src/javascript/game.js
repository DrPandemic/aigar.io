import $ from "jquery";
import * as constants from "./constants";
import sort from "immutable-sort";

let canvasWidth = 0;
let canvasHeight = 0;

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

let mouseIsDown = false;

function drawCircle(context, position, radius, color) {
  context.beginPath();
  context.arc(position.x, position.y, radius, 0, Math.PI * 2, false);
  context.fillStyle = color;
  context.fill();
}

function writeCellTeamName(playerName, context, position){
  context.fillStyle = constants.textColor;
  context.font = constants.textStyle;
  context.textAlign="center";
  context.textBaseline = "middle";
  context.strokeStyle = constants.textBorderColor;

  context.fillText(playerName, position.x, position.y);
  context.strokeText(playerName, position.x, position.y);
}

export function createGameCanvas() {
  return document.createElement("canvas");
}

export function initMap(canvas, map) {
  canvas.width = map.width;
  canvasWidth = map.width;
  canvas.height = map.height;
  canvasHeight = map.height;

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

export function drawPlayersOnMap(players, canvas, drawNames) {
  const context = canvas.getContext("2d");
  for(const player of players) {
    const color = getPlayerColor(players, player);
    for(const cell of player.cells) {
      drawCircle(context, cell.position, cell.radius, color);
      if (drawNames) writeCellTeamName(player.name, context, cell.position);
    }
  }
}

export function drawResourcesOnMap(resources, canvas) {
  const context = canvas.getContext("2d");
  const drawResources = (resources, color, rgba, mass) => {
    for(const resource of resources) {
      const grid = context.createRadialGradient(resource.x,resource.y, .5, resource.x, resource.y,constants.resourceMass);
      grid.addColorStop(0,color);
      grid.addColorStop(1, rgba);
      drawCircle(context, resource, mass, grid);
    }
  };

  drawResources(resources.regular, constants.regularColor, constants.regularRGBColor, constants.regularResourceMass);
  drawResources(resources.silver, constants.silverColor, constants.silverRGBColor, constants.resourceMass);
  drawResources(resources.gold, constants.goldColor, constants.goldRGBColor,  constants.resourceMass);
}

export function drawMap(canvas) {
  screenContext.clearRect(0, 0, screenWidth, screenHeight);
  screenContext.drawImage(canvas, xScreenPosOnMap, yScreenPosOnMap, screenWidth, screenHeight, 0, 0, screenWidth, screenHeight);
}

export function initMiniMap(canvas, players) {
  const tempCanvas = document.createElement("canvas");
  let context = tempCanvas.getContext("2d");
  miniMapHeight = miniMapWidth*canvas.height/canvas.width;

  //set dimensions
  miniMapCanvas.width = miniMapHeight;
  miniMapCanvas.height = miniMapHeight;
  tempCanvas.width = canvasWidth;
  tempCanvas.height = canvasHeight;

  //MiniMap background
  miniMapContext.rect(0, 0, canvasWidth, canvasHeight);
  miniMapContext.fillStyle = "rgba(58, 58, 58, 0.85)";
  miniMapContext.fill();

  drawPlayersOnMap(players, tempCanvas, false);
  miniMapContext.drawImage(tempCanvas, 0, 0, miniMapWidth, miniMapHeight);
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

export function setFocusScreen(position) {
  let newPosition = {
    x: position.x - (screenWidth/2),
    y: position.y - (screenHeight/2)
  };
  newPosition = keepInsideMap(newPosition, canvasWidth, screenWidth, canvasHeight, screenHeight);
  xScreenPosOnMap = newPosition.x;
  yScreenPosOnMap = newPosition.y;
}

function changeScreenPos(mousePos) {
  let miniMapPos = {
    x : (mousePos.x - miniMapPosX) - (miniMapScreenPosWidth / 2),
    y : mousePos.y - (miniMapScreenPosHeight / 2)
  };
  miniMapPos = keepInsideMap(miniMapPos, miniMapWidth, miniMapScreenPosWidth, miniMapHeight, miniMapScreenPosHeight);

  xScreenPosOnMap = miniMapPos.x * screenToMapRatioWidth * (screenWidth/miniMapWidth);
  yScreenPosOnMap = miniMapPos.y * screenToMapRatioHeight * (screenHeight/miniMapHeight);
}

function keepInsideMap(pos, bigWidth, smallWidth, bigHeight, smallHeight) {
  if (pos.x < 0) {
    pos.x = 0;
  } else if (pos.x > bigWidth - smallWidth) {
    pos.x = bigWidth - smallWidth;
  }

  if (pos.y < 0) {
    pos.y = 0;
  } else if (pos.y > bigHeight - smallHeight) {
    pos.y = bigHeight - smallHeight;
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

function mouseClick(e) {
  const mousePos = getMousePos(e);
  if (mousePos.x > miniMapPosX && mousePos.y < miniMapHeight) {
    changeScreenPos(mousePos);
  }
}

screenCanvas.onmousedown = function(e) {
  mouseIsDown = true;
  mouseClick(e);
};
screenCanvas.onmouseup = function(e) {
  if(mouseIsDown) mouseClick(e);
  mouseIsDown = false;
};

screenCanvas.onmousemove = function(e) {
  if(!mouseIsDown) return false;
  mouseClick(e);
  return false;
};

export function drawGame(gameState, canvas) {
  initMap(canvas, gameState.map);
  initMiniMap(canvas, gameState.players);
  drawResourcesOnMap(gameState.resources, canvas);
  drawPlayersOnMap(gameState.players, canvas, true);
  drawMap(canvas);
  drawMiniMap(canvas);
}
