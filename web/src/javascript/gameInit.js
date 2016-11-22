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
