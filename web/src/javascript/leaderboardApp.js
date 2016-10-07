import {fetchEntries} from "./leaderboardEntry";

function displayEntries(entries) {
  const leaderboard = document.getElementById("leaderboard-body");
  for(const entry of entries) {
    const row = leaderboard.insertRow(0);
    row.insertCell(0).innerHTML = entry.team_id;
    row.insertCell(1).innerHTML = entry.name;
    row.insertCell(2).innerHTML = entry.score;
  }
}

fetchEntries()
  .then((entries) => {
    displayEntries(entries);
  });
