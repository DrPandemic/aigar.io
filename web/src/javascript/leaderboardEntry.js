export class LeaderboardEntry {
  constructor(player_id, name, score, timestamp) {
    this.player_id = player_id;
    this.name = name;
    this.score = score;
    this.timestamp = new Date(timestamp);
  }
}
