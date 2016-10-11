import {fetchEntries} from "./leaderboardEntry";
import he from "he";

function displayEntries(entries) {
  const leaderboard = document.getElementById("leaderboard-body");
  for(const entry of entries) {
    const row = leaderboard.insertRow(0);
    row.insertCell(0).innerHTML = he.encode(entry.team_id.toString());
    row.insertCell(1).innerHTML = he.encode(entry.name);
    row.insertCell(2).innerHTML = he.encode(entry.score.toString());
  }
}

fetchEntries()
  .then((entries) => {
    displayEntries(entries);
  });
