import {sendAdminRequest} from "./network";

const seedButton = document.getElementById("seed-button");
seedButton.onclick = () => {
  sendAdminRequest("player", "post", {seed: true})
    .then(() => alert("The DB was seeded"))
    .catch(e => alert(e));
};

document.getElementById("reset-button").onclick = () => {
  sendAdminRequest("competition", "put", {running: true})
    .then(() => alert("The thread was reset"))
    .catch(e => alert(e));
};

document.getElementById("duration-button").onclick = () => {
  const duration = parseInt(document.getElementById("duration-input").value) * 60;
  sendAdminRequest("ranked", "put", {duration})
    .then(() => alert("The duration was set"))
    .catch(e => alert(e));
};

document.getElementById("new-player-button").onclick = () => {
  const playerName = document.getElementById("new-player-input").value;
  sendAdminRequest("player", "post", {player_name: playerName})
    .then(response => {
      document.getElementById("new-player-secret").value = response.player_secret;
      document.getElementById("new-player-id").value = response.player_id;
      alert("A new player was created");
    })
    .catch(e => alert(e));
};

document.getElementById("new-player-secret-button").onclick = () => {
  document.getElementById("new-player-secret").select();
  document.execCommand("copy");
};
document.getElementById("new-player-id-button").onclick = () => {
  document.getElementById("new-player-id").select();
  document.execCommand("copy");
};
