import he from "he";
import {getPlayerColor} from "./drawMap";

export function drawLeaderboard(state) {
  const leaderboard = document.getElementById("leaderboard-body");
  const new_tbody = document.createElement("tbody");
  new_tbody.setAttribute("id", "leaderboard-body");
  const players = state.players.sort((a, b) => a.score - b.score);

  for(const player of players) {
    const color = getPlayerColor(state.players, player);
    const row = new_tbody.insertRow(0);
    row.insertCell(0).innerHTML = `<div class="color-box" style="background-color: ${color};"></div>`;
    row.insertCell(1).innerHTML = he.encode(player.id.toString());
    row.insertCell(2).innerHTML = he.encode(player.name);
    row.insertCell(3).innerHTML = he.encode(player.total_mass.toString());
  }

  leaderboard.parentNode.replaceChild(new_tbody, leaderboard);
}
