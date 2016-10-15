const apiURL = "http://localhost:1337/api/1/leaderboard/";

export class LeaderBoardEntry {
  constructor(team_id, name, score) {
    this.team_id = team_id;
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
        entry.team_id,
        entry.name,
        entry.score)
      ).sort((a, b) => a.score - b.score);
    }).catch((err) => console.error(err));
}
