import he from "he";
import {getPlayerColor} from "./drawMap";
import {setFocusScreen} from "./drawMap";
import sort from "immutable-sort";

export function drawLeaderboard(state) {
  const leaderboard = document.getElementById("leaderboard");
  const new_tbody = document.createElement("tbody");
  new_tbody.setAttribute("id", "leaderboard-body");
  const players = sort(state.players, (a, b) => b.total_mass - a.total_mass);

  for(const player of players) {
    const color = getPlayerColor(players, player);
    const row = new_tbody.insertRow();
    row.onclick = function() { 
        focusOnPlayer(player.id, state);
      };
    if(!player.isActive) {
      row.className = "inactive-player";
    }
    row.insertCell(0).innerHTML = `<div class="color-box" style="background-color: ${color};"></div>`;
    row.insertCell(1).innerHTML = he.encode(player.id.toString());
    row.insertCell(2).innerHTML = he.encode(player.name);
    row.insertCell(3).innerHTML = he.encode(player.total_mass.toString());
  }

  const old = document.getElementById("leaderboard-body");
  leaderboard.replaceChild(new_tbody, old);
}

function focusOnPlayer(id, state){
    var cellToFocus = findBiggestCell(findPlayerWithId(id, state.players).cells);
    if(cellToFocus != null){
        setFocusScreen(cellToFocus.position);
    }
}

function findPlayerWithId(id, players){
  for(const player of players) {
    if(player.id == id){
      return player;
    }
  }
}

function findBiggestCell(cells){
  if (cells.length > 0){
    var biggestCell = cells[0];
    for(const cell of cells) {
      if(cell.radius > biggestCell.radius){
        biggestCell = cell;
      }
    }
    return biggestCell;
  }
  return null;
}
