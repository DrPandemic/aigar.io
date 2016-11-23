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
