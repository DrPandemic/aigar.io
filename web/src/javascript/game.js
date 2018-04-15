import * as constants from "./constants";
import sort from "immutable-sort";
import {updateTimeLeft, resizeCanvas, createCanvas} from "./gameUI";

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

export let cellFocused = null;

const circleCache = {};
const resourceCache = [
  [constants.regularColor, constants.regularRGBColor, constants.regularResourceMass],
  [constants.silverColor, constants.silverRGBColor, constants.resourceMass],
  [constants.goldColor, constants.goldRGBColor, constants.resourceMass],
].map(([c, r, m]) => prerenderResource(c, r, m));
const virusCache = {};
const nameCache = {};

function prerenderResource(color, rgba, radius) {
  const canvas = createCanvas();
  resizeCanvas(canvas, radius * 2, radius * 2);
  const context = canvas.getContext("2d");

  const grid = context.createRadialGradient(radius, radius, .5, radius, radius, constants.resourceMass);
  grid.addColorStop(0, color);
  grid.addColorStop(1, rgba);
  drawCircle(context, {x: radius, y: radius}, radius, grid);

  return canvas;
}

function drawCircle(context, position, radius, color, drawBorder = false) {
  const key = JSON.stringify([radius, color, drawBorder]);
  let canvas = circleCache[key];
  let diff = radius;
  if (drawBorder) {
    diff += constants.highlightThickness;
  }
  if (!canvas) {
    canvas = createCanvas();
    resizeCanvas(canvas, diff * 2 + 1, diff * 2 + 1);
    circleCache[key] = canvas;
    const newContext = canvas.getContext("2d");
    newContext.beginPath();
    newContext.arc(diff, diff, radius, 0, Math.PI * 2, false);
    if(drawBorder){
      newContext.lineWidth = constants.highlightThickness;
      newContext.strokeStyle = constants.highlightColor;
      newContext.stroke();
    }
    newContext.fillStyle = color;
    newContext.fill();
  }

  context.drawImage(canvas, position.x - diff, position.y - diff);
}

function writeCellTeamName(playerName, mapContext, position) {
  let canvas = nameCache[playerName];
  if (!canvas) {
    canvas = createCanvas();
    const context = canvas.getContext("2d");
    resizeCanvas(canvas, 700, 30);

    context.beginPath();
    context.fillStyle = constants.textColor;
    context.font = constants.textStyle;
    context.textAlign = "center";
    context.textBaseline = "middle";
    context.lineWidth = constants.textBorderThickness;
    context.strokeStyle = constants.textBorderColor;
    context.fillText(playerName, 350, 13);
    context.strokeText(playerName, 350, 13);

    nameCache[playerName] = canvas;
  }
  mapContext.drawImage(canvas, position.x - 350, position.y - 13);
}

function initGlobals(gameCanvas, map) {
  const screenCanvas = document.getElementById("screenCanvas");

  canvasWidth = map.width;
  canvasHeight = map.height;

  screenWidth = document.getElementById("gameDiv").offsetWidth - constants.scrollBarWidth;
  screenHeight = screenWidth * constants.ratioHeight;
  screenCanvas.width = screenWidth;
  screenCanvas.height = screenHeight;
  miniMapWidth = screenWidth / 4;
  miniMapPosX = screenWidth - miniMapWidth;

  screenToMapRatioWidth = gameCanvas.width / screenCanvas.width;
  screenToMapRatioHeight = gameCanvas.height / screenCanvas.height;
  miniMapScreenPosWidth = miniMapWidth / screenToMapRatioWidth;
  miniMapScreenPosHeight = miniMapHeight / screenToMapRatioHeight;
}

export function initMap(gameCanvas) {
  if(!resizeCanvas(gameCanvas, canvasWidth, canvasHeight)) {
    const context = gameCanvas.getContext("2d");
    context.clearRect(0, 0, canvasWidth, canvasHeight);
  }
}

export function getPlayerColor(players, currentPlayer) {
  const playerPosition = sort(players, (a, b) => a.id - b.id)
    .findIndex(player => player.id === currentPlayer.id);
  return constants.playerColors[playerPosition];
}

export function drawPlayersOnMap(players, gameCanvas, onMinimap, ratio = 1) {
  const context = gameCanvas.getContext("2d");
  let cellArray = [];
  for(const player of players) {
    const color = getPlayerColor(players, player);
    for(const cell of player.cells) {
      cellArray.push({
        position: {
          x: cell.position.x / ratio,
          y: cell.position.y / ratio,
        },
        radius: cell.radius / ratio,
        color: color,
        playerName: player.name,
        target: cell.target,
        playerId: player.id,
        id: cell.id
      });
    }
  }

  const cellsToDraw = sort(cellArray, (a, b) => a.radius - b.radius);
  for(const cell of cellsToDraw){
    if(cell.playerId === playerFocused && cell.id === cellFocused.id) {
      drawCircle(context, cell.position, cell.radius, cell.color, true);
    }
    else {
      drawCircle(context, cell.position, cell.radius, cell.color);
    }
    if (!onMinimap) {
      writeCellTeamName(cell.playerName, context, cell.position);
    }
    const targetLinesBtn = document.getElementById("targetLinesBtn");
    if (targetLinesBtn.className === "btn btn-primary" && !onMinimap) {
      drawLine(context, cell.position, cell.target, cell.color);
    }
  }
}

export function drawLine(context, position, target, color, width = constants.targetLineThickness) {
  context.beginPath();
  context.moveTo(position.x, position.y);
  context.lineTo(target.x, target.y);
  context.lineWidth = width;
  context.strokeStyle = color;
  context.stroke();
}

export function drawResourcesOnMap(resources, gameCanvas) {
  const context = gameCanvas.getContext("2d");
  const drawResources = (resources, resourceCanvas) => {
    for(const resource of resources) {
      context.drawImage(resourceCanvas, resource.x, resource.y);
    }
  };

  drawResources(resources.regular, resourceCache[0]);
  drawResources(resources.silver, resourceCache[1]);
  drawResources(resources.gold, resourceCache[2]);
}

export function drawVirusesOnMap(viruses, gameCanvas) {
  const context = gameCanvas.getContext("2d");

  for(const virus of viruses) {
    let canvas = virusCache[virus.radius];
    if (!canvas) {
      canvas = drawVirusShape(virus.radius);
      virusCache[virus.radius] = canvas;
    }
    context.drawImage(canvas, virus.position.x, virus.position.y);
  }
}

function drawVirusShape(radius) {
  const canvas = createCanvas();
  resizeCanvas(canvas, radius * 2 + 5, radius * 2 + 5);
  const context = canvas.getContext("2d");
  const color = context.createRadialGradient(radius, radius, 5, radius, radius, radius);
  color.addColorStop(0, constants.virusColor);
  color.addColorStop(1, constants.virusEndColor);
  let rot = Math.PI / 2 * 3;
  let x = radius;
  let y = radius;
  let step = Math.PI / constants.numberOfSpikes;
  let innerRadius = radius - 5;

  context.beginPath();
  context.moveTo(radius, 0);
  for (let i = 0; i < constants.numberOfSpikes; i++) {
    x = radius + Math.cos(rot) * radius;
    y = radius + Math.sin(rot) * radius;
    context.lineTo(x, y);
    rot += step;

    x = radius + Math.cos(rot) * innerRadius;
    y = radius + Math.sin(rot) * innerRadius;
    context.lineTo(x, y);
    rot += step;
  }
  context.lineTo(radius, 0);
  context.closePath();
  context.fillStyle = color;
  context.fill();
  return canvas;
}

function drawBackgroud(screenCanvas) {
  const screenContext = screenCanvas.getContext("2d");
  const screenWidth = screenCanvas.width;
  const screenHeight = screenCanvas.height;
  screenContext.fillStyle = "#fafafa";
  screenContext.fillRect(0, 0, screenWidth, screenHeight);

  for (let i = -xScreenPosOnMap % constants.pixelBetweenBackgroundLines; i <= screenWidth; i += constants.pixelBetweenBackgroundLines) {
    drawLine(screenContext, {x: i, y: 0}, {x: i, y: screenHeight}, "#000", constants.backgroundLineThickness);
  }
  for (let i = -yScreenPosOnMap % constants.pixelBetweenBackgroundLines; i <= screenHeight; i += constants.pixelBetweenBackgroundLines) {
    drawLine(screenContext, {x: 0, y: i}, {x: screenWidth, y: i}, "#000", constants.backgroundLineThickness);
  }
}

export function drawMap(gameCanvas) {
  const screenCanvas = document.getElementById("screenCanvas");
  const screenContext = screenCanvas.getContext("2d");
  const screenWidth = screenCanvas.width;
  const screenHeight = screenCanvas.height;
  drawBackgroud(screenCanvas);
  screenContext.drawImage(gameCanvas, xScreenPosOnMap, yScreenPosOnMap, screenWidth, screenHeight, 0, 0, screenWidth, screenHeight);
}

export function initMiniMap(gameCanvas, miniMapCanvas, miniMapTmpCanvas, players) {
  miniMapHeight = miniMapWidth * gameCanvas.height / gameCanvas.width;

  //set dimensions
  resizeCanvas(miniMapCanvas, miniMapWidth, miniMapHeight);

  //MiniMap background
  const miniMapContext = miniMapCanvas.getContext("2d");
  miniMapContext.clearRect(0, 0, canvasWidth, canvasHeight);
  miniMapContext.rect(0, 0, miniMapWidth, miniMapHeight);
  miniMapContext.fillStyle = constants.miniMapColor;
  miniMapContext.fill();

  drawPlayersOnMap(players, miniMapCanvas, true, gameCanvas.width / miniMapWidth);
}

export function drawMiniMap(gameCanvas, miniMapCanvas) {
  const screenCanvas = document.getElementById("screenCanvas");
  const screenContext = screenCanvas.getContext("2d");

  drawMiniMapScreenPos(gameCanvas, miniMapCanvas);
  screenContext.drawImage(miniMapCanvas, miniMapPosX, 0);
}

function findMiniMapScreenPositionPlayer(players){
  if(playerFocused != null){
    let player = players.find(p => p.id === playerFocused);
    checkIfCellFocusedStillExists(player.cells);
    setFocusScreen(cellFocused.position);
  }
}

export function findNextCell(cells, playerId){
  let cell = cells[0];
  if(playerFocused === playerId){
    for(let i = 0; i < cells.length; i++){
      if(cellFocused.id === cells[i].id){
        cell = cells[i + 1];

        if(!cell) {
          cell = cells[0];
        }
        break;
      }
    }
  }
  else{
    playerFocused = playerId;
  }
  cellFocused = cell;
}

function checkIfCellFocusedStillExists(cells){
  let cell = cells.find(p => p.id === cellFocused.id);
  if(!cell){
    cellFocused = cells[0];
  }
  else{
    cellFocused = cell;
  }
}

function drawMiniMapScreenPos(canvas, miniMapCanvas) {
  const miniMapContext = miniMapCanvas.getContext("2d");

  miniMapContext.strokeStyle = "#fff";
  const xMiniMapPos = miniMapWidth / canvas.width * xScreenPosOnMap;
  const yMiniMapPos = miniMapHeight / canvas.height * yScreenPosOnMap;
  miniMapContext.strokeRect(xMiniMapPos, yMiniMapPos, miniMapScreenPosWidth, miniMapScreenPosHeight);
}

export function setFocusScreen(position) {
  const screenCanvas = document.getElementById("screenCanvas");
  const screenWidth = screenCanvas.width;
  const screenHeight = screenCanvas.height;

  let newPosition = {
    x: position.x - (screenWidth / 2),
    y: position.y - (screenHeight / 2)
  };
  newPosition = keepInsideMap(newPosition, canvasWidth, screenWidth, canvasHeight, screenHeight);
  xScreenPosOnMap = newPosition.x;
  yScreenPosOnMap = newPosition.y;
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

  xScreenPosOnMap = miniMapPos.x * screenToMapRatioWidth * (screenWidth / miniMapWidth);
  yScreenPosOnMap = miniMapPos.y * screenToMapRatioHeight * (screenHeight / miniMapHeight);
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
    if(mouseIsDown) {
      mouseClick(e);
    }
    mouseIsDown = false;
  };

  screenCanvas.onmousemove = function(e) {
    if(!mouseIsDown) {
      return false;
    }
    mouseClick(e);
    return false;
  };
}

export function prepareCanvases(gameState, gameCanvas, miniMapCanvas, miniMapTmpCanvas) {
  initMap(gameCanvas, gameState.map);
  findMiniMapScreenPositionPlayer(gameState.players);
  initMiniMap(gameCanvas, miniMapCanvas, miniMapTmpCanvas, gameState.players);
  drawResourcesOnMap(gameState.resources, gameCanvas);
  drawVirusesOnMap(gameState.viruses, gameCanvas);
  drawPlayersOnMap(gameState.players, gameCanvas, false);

  updateTimeLeft(gameState.timeLeft);
}

export function drawGame(gameState, gameCanvas, miniMapCanvas) {
  initGlobals(gameCanvas, gameState.map);
  drawMap(gameCanvas);
  drawMiniMap(gameCanvas, miniMapCanvas);
}

function interpolate(prev, next, ratio) {
  return (1 - ratio) * prev + ratio * next;
}

export function interpolateState(prev, next, ratio) {
  if(ratio <= 0) {
    return prev;
  }
  if(ratio >= 1) {
    return next;
  }

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
