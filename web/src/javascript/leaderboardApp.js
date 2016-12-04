import {fetchLeaderboardEntries} from "./network";
import he from "he";

function displayEntries(entries) {
  const leaderboard = document.getElementById("leaderboard-body");
  for(const entry of entries) {
    const row = leaderboard.insertRow(0);
    row.insertCell(0).innerHTML = he.encode(entry.player_id.toString());
    row.insertCell(1).innerHTML = he.encode(entry.name);
    row.insertCell(2).innerHTML = he.encode(entry.score.toFixed(2).toString());
  }
}

function clearEntries() {
  const leaderboard = document.getElementById("leaderboard-body");
  while (leaderboard.firstChild) {
    leaderboard.removeChild(leaderboard.firstChild);
  }
}

function fetchAndDisplay() {
  return fetchLeaderboardEntries()
    .then((entries) => {
      clearEntries();
      displayEntries(entries);
    });
}

setInterval(fetchAndDisplay, 5 * 1000);

fetchAndDisplay();
