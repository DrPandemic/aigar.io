import * as constants from "./constants";
import sort from "immutable-sort";
import {resizeCanvas, createCanvas} from "./gameUI";

function prerenderResource(state, color, radius) {
  const canvas = createCanvas();
  resizeCanvas(canvas, radius * 2, radius * 2);
  const context = canvas.getContext("2d");

  drawCircle(state, context, {x: radius, y: radius}, radius, color);

  return canvas;
}

function drawCircle(state, context, position, radius, color, drawBorder = false) {
  const key = JSON.stringify([radius, color, drawBorder]);
  let canvas = state.circleCache[key];
  let diff = radius;
  if (drawBorder) {
    diff += constants.highlightThickness;
  }
  if (!canvas) {
    canvas = createCanvas();
    resizeCanvas(canvas, diff * 2 + 1, diff * 2 + 1);
    state.circleCache[key] = canvas;
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

function drawBurst(context, position, radius, color, target) {
  let v = {
    x: target.x - position.x,
    y: target.y - position.y
  };
  let dir = {
    x: v.x / Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2)),
    y: v.y / Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2))
  };

  let lineStart = calculateLineStartOrEnd(-1, 1, dir, radius, constants.burstLineDistance, position);
  let lineEnd = calculateLineStartOrEnd(-1, 1, dir, radius, constants.burstLineDistance + constants.burstLineLength, position);
  drawLine(context, lineStart, lineEnd, constants.highlightColor, constants.burstLineThickness);

  lineStart = calculateLineStartOrEnd(-1, 1, dir, (radius / 2), constants.burstLineDistance + radius, position);
  lineEnd = calculateLineStartOrEnd(-1, 1, dir, (radius / 2), constants.burstLineDistance + constants.burstLineLength + radius, position);
  drawLine(context, lineStart, lineEnd, constants.highlightColor, constants.burstLineThickness);

  lineStart = calculateLineStartOrEnd(1, -1, dir, (radius / 2), constants.burstLineDistance + radius, position);
  lineEnd = calculateLineStartOrEnd(1, -1, dir, (radius / 2), constants.burstLineDistance + constants.burstLineLength + radius, position);
  drawLine(context, lineStart, lineEnd, constants.highlightColor, constants.burstLineThickness);

  lineStart = calculateLineStartOrEnd(1, -1, dir, radius, constants.burstLineDistance, position);
  lineEnd = calculateLineStartOrEnd(1, -1, dir, radius, constants.burstLineDistance + constants.burstLineLength, position);
  drawLine(context, lineStart, lineEnd, constants.highlightColor, constants.burstLineThickness);
}

function calculateLineStartOrEnd(dirxMul, diryMul, dir, radius, length, position) {
  let linePos = {
    x: (position.x + (diryMul * dir.y * radius)) + (-dir.x * length),
    y: (position.y + (dirxMul * dir.x * radius)) + (-dir.y * length)
  };

  return linePos;
}

function writeCellTeamName(state, playerName, mapContext, position) {
  let canvas = state.nameCache[playerName];
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

    state.nameCache[playerName] = canvas;
  }
  mapContext.drawImage(canvas, position.x - 350, position.y - 13);
}

export function initState() {
  const state = {
    gameCanvas: createCanvas(),
    screenCanvas: document.getElementById("screenCanvas"),
    miniMapCanvas: createCanvas(),
    miniMapTmpCanvas: createCanvas(),
    circleCache: {},
    virusCache: {},
    nameCache: {},
    display: {
      screenWidth: 0,
      screenHeight: 0,
      xScreenPosOnMap: 0,
      yScreenPosOnMap: 0,
      canvasWidth: 0,
      canvasHeight: 0,
      screenToMapRatioWidth: 0,
      screenToMapRatioHeight: 0,
    },
  };
  const resourceCache = [
    [constants.regularColor, constants.regularResourceMass],
    [constants.silverColor, constants.resourceMass],
    [constants.goldColor, constants.resourceMass],
  ].map(([c, m]) => ({canvas: prerenderResource(state, c, m), radius: m}));
  state.resourceCache = resourceCache;
  return state;
}

export function refreshState(state) {
  state.display.screenWidth = document.getElementById("gameDiv").offsetWidth - constants.scrollBarWidth;
  state.display.screenHeight = state.display.screenWidth * constants.ratioHeight;
  resizeCanvas(state.screenCanvas, state.display.screenWidth, state.display.screenHeight);
  state.display.miniMapWidth = state.display.screenWidth / 4;
  state.display.miniMapPosX = state.display.screenWidth - state.display.miniMapWidth;

  state.display.screenToMapRatioWidth = state.gameCanvas.width / state.screenCanvas.width;
  state.display.screenToMapRatioHeight = state.gameCanvas.height / state.screenCanvas.height;
  state.display.miniMapScreenPosWidth = state.display.miniMapWidth / state.display.screenToMapRatioWidth;
  state.display.miniMapScreenPosHeight = state.display.miniMapHeight / state.display.screenToMapRatioHeight;
}

export function initMap({gameCanvas, display: {canvasWidth, canvasHeight}}) {
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

export function drawPlayersOnMap(state, canvas, onMinimap, ratio = 1) {
  const {game: {players}, display: {playerFocused, cellFocused}} = state;
  const context = canvas.getContext("2d");
  const cellArray = [];
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
        id: cell.id,
        burst: cell.burst,
      });
    }
  }

  for(const cell of sort(cellArray, (a, b) => a.radius - b.radius)) {
    if(cell.playerId === playerFocused && cell.id === cellFocused.id) {
      drawCircle(state, context, cell.position, cell.radius, cell.color, true);
    }
    else {
      drawCircle(state, context, cell.position, cell.radius, cell.color);
    }

    if (!onMinimap) {
      writeCellTeamName(state, cell.playerName, context, cell.position);
    }

    if(cell.burst && !onMinimap){
      drawBurst(context, cell.position, cell.radius, cell.color, cell.target);
    }

    const targetLinesBtn = document.getElementById("targetLinesBtn");
    if (targetLinesBtn.className === "btn btn-primary" && !onMinimap) {
      drawLine(context, cell.position, cell.target, cell.color);
    }
  }
}

export function drawLine(context, position, target, color = "#000000", width = constants.targetLineThickness) {
  context.beginPath();
  context.moveTo(position.x, position.y);
  context.lineTo(target.x, target.y);
  context.lineWidth = width;
  context.strokeStyle = color;
  context.stroke();
}

export function drawResourcesOnMap({game: {resources}, gameCanvas, resourceCache}) {
  const context = gameCanvas.getContext("2d");
  const drawResources = (resources, {canvas, radius}) => {
    for(const resource of resources) {
      context.drawImage(canvas, resource.x - radius, resource.y - radius);
    }
  };

  drawResources(resources.regular, resourceCache[0]);
  drawResources(resources.gold, resourceCache[2]);
  drawResources(resources.silver, resourceCache[1]);
}

export function drawVirusesOnMap(state) {
  const {gameCanvas, game: {viruses}} = state;
  const context = gameCanvas.getContext("2d");

  for(const virus of viruses) {
    let canvas = state.virusCache[virus.radius];
    if (!canvas) {
      canvas = drawVirusShape(virus.radius);
      state.virusCache[virus.radius] = canvas;
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

function drawBackgroud({display: {screenWidth, screenHeight, xScreenPosOnMap, yScreenPosOnMap}, screenCanvas}) {
  const screenContext = screenCanvas.getContext("2d");
  screenContext.fillStyle = "#fafafa";
  screenContext.fillRect(0, 0, screenWidth, screenHeight);

  for (let i = -xScreenPosOnMap % constants.pixelBetweenBackgroundLines; i <= screenWidth; i += constants.pixelBetweenBackgroundLines) {
    drawLine(screenContext, {x: i, y: 0}, {x: i, y: screenHeight}, "#000", constants.backgroundLineThickness);
  }
  for (let i = -yScreenPosOnMap % constants.pixelBetweenBackgroundLines; i <= screenHeight; i += constants.pixelBetweenBackgroundLines) {
    drawLine(screenContext, {x: 0, y: i}, {x: screenWidth, y: i}, "#000", constants.backgroundLineThickness);
  }
}

export function drawMap(state) {
  const screenContext = state.screenCanvas.getContext("2d");
  drawBackgroud(state);
  screenContext.drawImage(
    state.gameCanvas,
    state.display.xScreenPosOnMap,
    state.display.yScreenPosOnMap,
    state.display.screenWidth,
    state.display.screenHeight,
    0,
    0,
    state.display.screenWidth,
    state.display.screenHeight
  );
}

export function initMiniMap(state) {
  const {gameCanvas, miniMapCanvas, display: {miniMapWidth}} = state;
  const miniMapHeight = miniMapWidth * gameCanvas.height / gameCanvas.width;
  state.display.miniMapHeight = miniMapHeight;

  //set dimensions
  resizeCanvas(miniMapCanvas, miniMapWidth, miniMapHeight);

  //MiniMap background
  const miniMapContext = miniMapCanvas.getContext("2d");
  miniMapContext.clearRect(0, 0, miniMapWidth, miniMapHeight);
  miniMapContext.rect(0, 0, miniMapWidth, miniMapHeight);
  miniMapContext.fillStyle = constants.miniMapColor;
  miniMapContext.fill();

  drawPlayersOnMap(state, miniMapCanvas, true, gameCanvas.width / miniMapWidth);
}

export function drawMiniMap(state) {
  const screenContext = state.screenCanvas.getContext("2d");

  drawMiniMapScreenPos(state);
  screenContext.drawImage(state.miniMapCanvas, state.display.miniMapPosX, 0);
}

function findMiniMapScreenPositionPlayer(state) {
  if(state.display.playerFocused != null) {
    const player = state.game.players.find(p => p.id === state.display.playerFocused);
    checkIfCellFocusedStillExists(player.cells);
    setFocusScreen(state);
  }
}

export function findNextCell(state, cells, playerId) {
  let cell = cells[0];
  if(state.display.playerFocused === playerId) {
    for(let i = 0; i < cells.length; i++) {
      if(state.display.cellFocused.id === cells[i].id) {
        cell = cells[i + 1];

        if(!cell) {
          cell = cells[0];
        }
        break;
      }
    }
  }
  else {
    state.display.playerFocused = playerId;
  }
  state.display.cellFocused = cell;
}

function checkIfCellFocusedStillExists(state, cells){
  let cell = cells.find(p => p.id === state.display.cellFocused.id);
  if(!cell){
    state.display.cellFocused = cells[0];
  }
  else{
    state.display.cellFocused = cell;
  }
}

function drawMiniMapScreenPos(state) {
  const miniMapContext = state.miniMapCanvas.getContext("2d");

  miniMapContext.strokeStyle = "#fff";
  const xMiniMapPos = state.display.miniMapWidth / state.gameCanvas.width * state.display.xScreenPosOnMap;
  const yMiniMapPos = state.display.miniMapHeight / state.gameCanvas.height * state.display.yScreenPosOnMap;
  miniMapContext.strokeRect(
    xMiniMapPos,
    yMiniMapPos,
    state.display.miniMapScreenPosWidth,
    state.display.miniMapScreenPosHeight
  );
}

export function setFocusScreen(state) {
  const position = state.display.cellFocused.position;

  let newPosition = {
    x: position.x - (state.display.screenWidth / 2),
    y: position.y - (state.display.screenHeight / 2)
  };
  newPosition = keepInsideMap(
    newPosition,
    state.display.canvasWidth,
    state.display.screenWidth,
    state.display.canvasHeight,
    state.display.screenHeight
  );
  state.display.xScreenPosOnMap = newPosition.x;
  state.display.yScreenPosOnMap = newPosition.y;
}

function changeScreenPos(state, mousePos) {
  const {miniMapPosX, miniMapScreenPosWidth, miniMapScreenPosHeight, miniMapWidth, miniMapHeight,
    screenToMapRatioWidth, screenToMapRatioHeight, screenWidth, screenHeight} = state.display;

  let miniMapPos = {
    x : (mousePos.x - miniMapPosX) - (miniMapScreenPosWidth / 2),
    y : mousePos.y - (miniMapScreenPosHeight / 2)
  };
  miniMapPos = keepInsideMap(miniMapPos, miniMapWidth, miniMapScreenPosWidth, miniMapHeight, miniMapScreenPosHeight);

  state.display.xScreenPosOnMap = miniMapPos.x * screenToMapRatioWidth * (screenWidth / miniMapWidth);
  state.display.yScreenPosOnMap = miniMapPos.y * screenToMapRatioHeight * (screenHeight / miniMapHeight);
}

function keepInsideMap(pos, bigWidth, smallWidth, bigHeight, smallHeight) {
  if (pos.x < 0) {
    pos.x = 0;
  } else if (pos.x > bigWidth - smallWidth) {
    pos.x = Math.max(0, bigWidth - smallWidth);
  }

  if (pos.y < 0) {
    pos.y = 0;
  } else if (pos.y > bigHeight - smallHeight) {
    pos.y = Math.max(0, bigHeight - smallHeight);
  }

  return pos;
}

export function initCanvas(state) {
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
    if (mousePos.x > state.display.miniMapPosX && mousePos.y < state.display.miniMapHeight) {
      state.display.playerFocused = null;
      changeScreenPos(state, mousePos);
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

export function prepareCanvases(state) {
  initMap(state);
  findMiniMapScreenPositionPlayer(state);
  initMiniMap(state);
  drawVirusesOnMap(state);
  drawResourcesOnMap(state);
  drawPlayersOnMap(state, state.gameCanvas, false);
}

export function drawGame(state) {
  refreshState(state);
  drawMap(state);
  drawMiniMap(state);

  return state;
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
