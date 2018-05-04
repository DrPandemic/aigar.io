import {sendAdminRequest} from "./network";

document.getElementById("seed-button").onclick = () => {
  if(confirm("This will delete everything from the database.")) {
    if(confirm("Are you really sure???")) {
      sendAdminRequest("player", "post", {seed: true, playerCount: 30})
        .then(() => alert("The DB was seeded."))
        .catch(e => alert(e));
    }
  }
};

document.getElementById("seed-without-players-button").onclick = () => {
  if(confirm("This will delete everything from the database.")) {
    if(confirm("Are you really sure???")) {
      sendAdminRequest("player", "post", {seed: true, playerCount: 0})
        .then(() => alert("The DB was seeded."))
        .catch(e => alert(e));
    }
  }
};

document.getElementById("reset-button").onclick = () => {
  if(confirm("This will stop the current game a start a new one.")) {
    sendAdminRequest("competition", "put", {running: true})
      .then(() => alert("The thread was reset."))
      .catch(e => alert(e));
  }
};

document.getElementById("duration-button").onclick = () => {
  const duration = parseInt(document.getElementById("duration-input").value) * 60;
  sendAdminRequest("ranked", "put", {duration})
    .then(() => alert("The duration was set."))
    .catch(e => alert(e));
};

document.getElementById("new-player-button").onclick = () => {
  const playerName = document.getElementById("new-player-input").value;
  sendAdminRequest("player", "post", {player_name: playerName})
    .then(response => {
      document.getElementById("new-player-secret").value = response.player_secret;
      document.getElementById("new-player-id").value = response.player_id;
      alert("A new player was created.");
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

document.getElementById("danger-zone-button").onclick = () => {
  const display = document.getElementById("danger-zone").style.display;
  document.getElementById("danger-zone").style.display = display === "none" ? "block" : "none";
};
