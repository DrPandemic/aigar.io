import * as constants from "./constants";
import sort from "immutable-sort";

let canvasWidth = 0;
let canvasHeight = 0;

let screenWidth;
let screenHeight;

let xScreenPosOnMap = 0;
let yScreenPosOnMap = 0;

let screenToMapRatioWidth;
let screenToMapRatioHeight;

let miniMapWidth;
let miniMapHeight;
let miniMapPosX;

let miniMapScreenPosWidth;
let miniMapScreenPosHeight;

let playerFocused = null;

function drawCircle(context, position, radius, color) {
  context.beginPath();
  context.arc(position.x, position.y, radius, 0, Math.PI * 2, false);
  context.fillStyle = color;
  context.fill();
}

function writeCellTeamName(playerName, context, position) {
  context.fillStyle = constants.textColor;
  context.font = constants.textStyle;
  context.textAlign="center";
  context.textBaseline = "middle";
  context.strokeStyle = constants.textBorderColor;

  context.fillText(playerName, position.x, position.y);
  context.strokeText(playerName, position.x, position.y);
}

export function createCanvas() {
  return document.createElement("canvas");
}

export function initMap(gameCanvas, map) {
  const screenCanvas = document.getElementById("screenCanvas");
  const context = gameCanvas.getContext("2d");

  if(!started) {
    gameCanvas.width = map.width;
    canvasWidth = map.width;
    gameCanvas.height = map.height;
    canvasHeight = map.height;
  } else {
    context.rect(0, 0, canvasWidth, canvasHeight);
    context.fillStyle = "rgba(0, 0, 0, 1)";
    context.fill();
  }

  screenWidth = document.getElementById("gameDiv").offsetWidth - constants.scrollBarWidth;
  screenHeight = screenWidth*constants.ratioHeight;
  screenCanvas.width = screenWidth;
  screenCanvas.height = screenHeight;
  miniMapWidth = screenWidth / 4;
  miniMapPosX = screenWidth - miniMapWidth;

  screenToMapRatioWidth = gameCanvas.width/ screenCanvas.width;
  screenToMapRatioHeight = gameCanvas.height/ screenCanvas.height;
  miniMapScreenPosWidth = miniMapWidth/screenToMapRatioWidth;
  miniMapScreenPosHeight = miniMapHeight/screenToMapRatioHeight;
}

export function getPlayerColor(players, currentPlayer) {
  const playerPosition = sort(players, (a, b) => a.id - b.id)
    .findIndex(player => player.id === currentPlayer.id);
  return constants.playerColors[playerPosition];
}

export function drawPlayersOnMap(players, gameCanvas, drawNames) {
  const context = gameCanvas.getContext("2d");
  let cellArray = [];
  for(const player of players) {
    const color = getPlayerColor(players, player);
    for(const cell of player.cells) {
      cellArray.push({
        position: cell.position,
        radius: cell.radius,
        color: color,
        playerName: player.name,
        target: cell.target
      });
    }
  }
  const cellsToDraw = sort(cellArray, (a, b) => a.radius - b.radius);
  for(const cell of cellsToDraw){
    drawCircle(context, cell.position, cell.radius, cell.color);
    if (drawNames) writeCellTeamName(cell.playerName, context, cell.position);
    const targetLinesBtn = document.getElementById("targetLinesBtn");
    if (targetLinesBtn.className === "btn btn-primary") drawCellTargetLines(context, cell.position, cell.target, cell.color);
  }
}

export function drawCellTargetLine(context, position, target, color) {
  context.beginPath();
  context.moveTo(position.x, position.y);
  context.lineTo(target.x, target.y);
  context.strokeStyle = color;
  context.stroke();
}

export function drawResourcesOnMap(resources, gameCanvas) {
  const context = gameCanvas.getContext("2d");
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

export function drawVirusesOnMap(viruses, gameCanvas) {
  const context = gameCanvas.getContext("2d");

  for(const virus of viruses) {
    const position = virus.position;
    const grad = context.createRadialGradient(position.x, position.y, 5, position.x, position.y, virus.radius);
    grad.addColorStop(0, constants.virusColor);
    grad.addColorStop(1, constants.virusEndColor);
    drawVirusShape(position, constants.numberOfSpikes, virus.radius, context, grad);
  }
}

function drawVirusShape(virus, spikes, outerRadius, context, color){
  let rot = Math.PI / 2 * 3;
  let x = virus.x;
  let y = virus.y;
  let step = Math.PI / spikes;
  let innerRadius = outerRadius-5;

  context.beginPath();
  context.moveTo(virus.x, virus.y - outerRadius);
  for (let i = 0; i < spikes; i++) {
    x = virus.x + Math.cos(rot) * outerRadius;
    y = virus.y + Math.sin(rot) * outerRadius;
    context.lineTo(x, y);
    rot += step;

    x = virus.x + Math.cos(rot) * innerRadius;
    y = virus.y + Math.sin(rot) * innerRadius;
    context.lineTo(x, y);
    rot += step;
  }
  context.lineTo(virus.x, virus.y - outerRadius);
  context.closePath();
  context.fillStyle=color;
  context.fill();
}

export function drawMap(gameCanvas) {
  const screenCanvas = document.getElementById("screenCanvas");
  const screenContext = screenCanvas.getContext("2d");
  const screenWidth = screenCanvas.width;
  const screenHeight = screenCanvas.height;

  screenContext.clearRect(0, 0, screenWidth, screenHeight);
  screenContext.drawImage(gameCanvas, xScreenPosOnMap, yScreenPosOnMap, screenWidth, screenHeight, 0, 0, screenWidth, screenHeight);
}

export function initMiniMap(gameCanvas, miniMapCanvas, miniMapTmpCanvas, players) {
  const miniMapContext = miniMapCanvas.getContext("2d");

  miniMapHeight = miniMapWidth*gameCanvas.height/gameCanvas.width;

  //set dimensions
  if(!started) {
    miniMapCanvas.width = miniMapHeight;
    miniMapCanvas.height = miniMapHeight;
    miniMapTmpCanvas.width = canvasWidth;
    miniMapTmpCanvas.height = canvasHeight;
  } else {
    const tmpContext = miniMapTmpCanvas.getContext("2d");
    tmpContext.rect(0, 0, canvasWidth, canvasHeight);
    tmpContext.fillStyle = "rgba(0, 0, 0, 1)";
    tmpContext.fill();
  }

  //MiniMap background
  miniMapContext.rect(0, 0, canvasWidth, canvasHeight);
  miniMapContext.fillStyle = "rgba(58, 58, 58, 0.85)";
  miniMapContext.fill();

  drawPlayersOnMap(players, miniMapTmpCanvas, false);
  miniMapContext.drawImage(miniMapTmpCanvas, 0, 0, miniMapWidth, miniMapHeight);
}

export function drawMiniMap(gameCanvas, miniMapCanvas) {
  const screenCanvas = document.getElementById("screenCanvas");
  const screenContext = screenCanvas.getContext("2d");

  drawMiniMapScreenPos(gameCanvas, miniMapCanvas);
  screenContext.drawImage(miniMapCanvas, miniMapPosX, 0);
  screenCanvas.style.background = "#000";
}

function findMiniMapScreenPositionPlayer(players){
  if(playerFocused != null){
    let player = players.find(p => p.id === playerFocused);
    setFocusScreen(findBiggestCell(player.cells).position);
  }
}

function drawMiniMapScreenPos(canvas, miniMapCanvas) {
  const miniMapContext = miniMapCanvas.getContext("2d");

  miniMapContext.strokeStyle = "#fff";
  const xMiniMapPos = miniMapWidth / canvas.width * xScreenPosOnMap;
  const yMiniMapPos = miniMapHeight / canvas.height * yScreenPosOnMap;
  miniMapContext.strokeRect(xMiniMapPos, yMiniMapPos, miniMapScreenPosWidth, miniMapScreenPosHeight);
}

export function setFocusScreen(position, id = playerFocused) {
  playerFocused = id;
  const screenCanvas = document.getElementById("screenCanvas");
  const screenWidth = screenCanvas.width;
  const screenHeight = screenCanvas.height;

  let newPosition = {
    x: position.x - (screenWidth/2),
    y: position.y - (screenHeight/2)
  };
  newPosition = keepInsideMap(newPosition, canvasWidth, screenWidth, canvasHeight, screenHeight);
  xScreenPosOnMap = newPosition.x;
  yScreenPosOnMap = newPosition.y;
}

export function findBiggestCell(cells) {
  if (cells.length > 0){
    let biggestCell = cells[0];
    for(const cell of cells) {
      if(cell.radius > biggestCell.radius){
        biggestCell = cell;
      }
    }
    return biggestCell;
  }
}

function changeScreenPos(mousePos) {
  const screenCanvas = document.getElementById("screenCanvas");
  const screenWidth = screenCanvas.width;
  const screenHeight = screenCanvas.height;

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

export function initCanvas() {
  const screenCanvas = document.getElementById("screenCanvas");
  let mouseIsDown = false;

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
      playerFocused = null;
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
}

export function drawGame(gameState, gameCanvas, miniMapCanvas, miniMapTmpCanvas) {
  initMap(gameCanvas, gameState.map);
  initMiniMap(gameCanvas, miniMapCanvas, miniMapTmpCanvas, gameState.players);
  drawResourcesOnMap(gameState.resources, gameCanvas);
  drawVirusesOnMap(gameState.viruses, gameCanvas);
  drawPlayersOnMap(gameState.players, gameCanvas, true);
  drawMap(gameCanvas);
  drawMiniMap(gameCanvas, miniMapCanvas);

  updateTimeLeft(gameState.timeLeft);
}

function updateTimeLeft(timeLeft){
  let myDate = new Date(timeLeft * 1000).toISOString().substr(11, 8);

  document.getElementById("timeLeft").value(myDate);
}

function interpolate(prev, next, ratio) {
  return (1 - ratio) * prev + ratio * next;
}

export function interpolateState(prev, next, ratio) {
  if(ratio <= 0) return prev;
  if(ratio >= 1) return next;

  const current = JSON.parse(JSON.stringify(prev));

  current.players = current.players.map(player => {
    player.cells = player.cells.map(cell => {
      let nextCell = next.players.find(p => p.id === player.id).cells.find(c => c.id === cell.id);
      // Only interpolate position when the cell is not dead
      if(nextCell) {
        cell.position.x = interpolate(cell.position.x, nextCell.position.x, ratio);
        cell.position.y = interpolate(cell.position.y, nextCell.position.y, ratio);
        cell.radius = interpolate(cell.radius, nextCell.radius, ratio);
      } else {
        cell.radius = interpolate(cell.radius, 0, ratio);
      }

      return cell;
    });

    return player;
  });

  return current;
}
