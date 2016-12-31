import {fetchLeaderboardEntries} from "./network";
import he from "he";

function displayEntries(entries) {
  const leaderboard = document.getElementById("leaderboard-body");
  for(const i in entries) {
    const entry = entries[i];
    const row = leaderboard.insertRow(0);
    row.insertCell(0).innerHTML = (entries.length - parseInt(i)).toString();
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
