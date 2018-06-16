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
    .then(entries => {
      const result = Object.entries(entries.reduce((acc, val) => {
        if (!acc[val.player_id]) {
          acc[val.player_id] = val;
        } else {
          acc[val.player_id].score += val.score;
        }

        return acc;
      }, {})).reduce((acc, [_, val]) => {
        acc.push(val);
        return acc;
      }, []);
      clearEntries();
      displayEntries(result);
    });
}

setInterval(fetchAndDisplay, 5 * 1000);

fetchAndDisplay();
