import he from "he";
import {getPlayerColor, setFocusScreen, findNextCell} from "./game";
import sort from "immutable-sort";

export function drawLeaderboard(state, gameState) {
  const tbody = document.getElementById("leaderboard-body");

  const sortedPlayers = sort(gameState.players, (a, b) => b.total_mass - a.total_mass);
  // Initial case
  if(tbody.rows.length !== sortedPlayers.length) {
    createTable(state, sortedPlayers, null, true);
    return;
  }

  const oldBounding = [... tbody.rows].reduce((result, row) => {
    result[row.id] = row.getBoundingClientRect();
    return result;
  }, {});

  createTable(state, sortedPlayers, oldBounding, false);
}

function createTable(state, players, oldBounding, firstTime) {
  const new_tbody = document.createElement("tbody");
  new_tbody.setAttribute("id", "leaderboard-body");

  for(const playerIndex in players) {
    const player = players[playerIndex];
    const color = getPlayerColor(players, player);

    const row = new_tbody.insertRow();
    row.id = `player-${player.id}`;
    row.onclick = function() {
      focusOnPlayer(state, player);
    };

    if(!player.isActive) {
      row.className = "inactive-player";
    }

    row.insertCell(0).innerHTML = `<div class="color-box" style="background-color: ${color};"></div>`;
    row.insertCell(1).innerHTML = (parseInt(playerIndex) + 1).toString();
    row.insertCell(2).innerHTML = he.encode(player.name);
    row.insertCell(3).innerHTML = he.encode(player.total_mass.toString());

    if(!firstTime) {
      addTransition(oldBounding, row, playerIndex);
    }
  }

  const old = document.getElementById("leaderboard-body");
  const leaderboard = document.getElementById("leaderboard");
  leaderboard.replaceChild(new_tbody, old);
  cleanEventListeners(old);
}

function addTransition(oldBounding, row, playerIndex) {
  const header = document.getElementById("leaderboard-header").getBoundingClientRect();

  const deltaY = oldBounding[row.id].top - (playerIndex * (header.bottom - header.top) + header.bottom);

  requestAnimationFrame(() => {
    row.style.transform = `translate(0, ${deltaY}px)`;
    row.style.transition = "transform 0s";
    requestAnimationFrame(() => {
      row.style.transform = "";
      row.style.transition = "transform 0.5s";
    });
  });
}

function cleanEventListeners(old) {
  for(const row of old.rows) {
    row.onclick = null;
  }
}

function focusOnPlayer(state, player) {
  findNextCell(state, player.cells, player.id);
  if(state.display.cellFocused) {
    setFocusScreen(state.display.cellFocused.position);
  }
}
