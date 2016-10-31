const apiURL = "http://localhost:1337/api/1/leaderboard/";

export class LeaderBoardEntry {
  constructor(player_id, name, score) {
    this.player_id = player_id;
    this.name = name;
    this.score = score;
  }
}

export function fetchEntries() {
  return fetch(apiURL, {method: "get"})
    .then((response) => {
      return response.json();
    }) .then((response) => {
      return response.data.map((entry) => new LeaderBoardEntry(
        entry.player_id,
        entry.name,
        entry.score)
      ).sort((a, b) => a.score - b.score);
    }).catch((err) => console.error(err));
}
