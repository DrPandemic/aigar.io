import * as constants from "./constants";

export function initLineButton() {
  const targetLinesBtn = document.getElementById("targetLinesBtn");
  targetLinesBtn.onclick = function() {
    if (targetLinesBtn.className === "btn btn-primary") {
      document.getElementById("targetLinesBtn").className = "btn btn-default";
    }
    else{
      document.getElementById("targetLinesBtn").className = "btn btn-primary";
    }
  };
}

export function updateTimeLeft(timeLeft){
  let myDate = new Date(timeLeft * 1000).toISOString().substr(11, 8);

  document.getElementById("timeLeft").innerText = myDate;
}

export function createCanvas() {
  return document.createElement("canvas");
}

export function resizeCanvas(canvas, width, height) {
  let resized = false;
  if(canvas.width !== width) {
    canvas.width = width;
    resized = true;
  }
  if(canvas.height !== height) {
    canvas.height = height;
    resized = true;
  }

  return resized;
}

function print(str) {
  const screenCanvas = document.getElementById("screenCanvas");
  const context = screenCanvas.getContext("2d");
  const screenWidth = screenCanvas.width;
  const screenHeight = screenCanvas.height;

  context.clearRect(0, 0, screenWidth, screenHeight);
  context.rect(0, 0, screenWidth, screenHeight);
  context.fillStyle = "#ffffff";
  context.fill();
  context.beginPath();
  context.fillStyle = constants.textBorderColor;
  context.font = constants.textStyle;
  context.textAlign="center";
  context.textBaseline = "middle";
  context.lineWidth = constants.textBorderThickness;

  context.fillText(str, screenWidth / 2, screenHeight / 2);
}

export function displayLoading() {
  let state = "Loading ";
  return setInterval(() => {
    print(state);
    state += ".";
    if(state === "Loading ....") {
      state = "Loading ";
    }
  }, 333);
}

export function hideLoading(handle) {
  clearInterval(handle);
}

export function displayDoesntExist() {
  print("This game is not currently running.");
}
