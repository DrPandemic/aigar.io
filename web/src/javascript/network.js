import {LeaderboardEntry} from "./leaderboardEntry";

const stateApiURL = "http://localhost:1337/api/1/game/";
const leaderboardApiURL = "http://localhost:1337/api/1/leaderboard/";


export function fetchState(gameId) {
  return fetch(`${stateApiURL}${gameId}`, {method: "get"})
    .then(response => {
      return response.json();
    })
    .then(response => {
      return response.data;
    });
}

export function fetchLeaderboardEntries() {
  return fetch(leaderboardApiURL, {method: "get"})
    .then((response) => {
      return response.json();
    }) .then((response) => {
      return response.data.map((entry) => new LeaderboardEntry(
        entry.player_id,
        entry.name,
        entry.score)
                              ).sort((a, b) => a.score - b.score);
    }).catch((err) => console.error(err));
}
