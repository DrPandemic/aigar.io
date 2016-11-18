import {sendAdminRequest} from "./network";

const seedButton = document.getElementById("seed-button");
seedButton.onclick = () => {
  sendAdminRequest("player", "post", {seed: true})
    .then(() => alert("The DB was seeded"))
    .catch(e => alert(e));
};

const resetButton = document.getElementById("reset-button");
resetButton.onclick = () => {
  sendAdminRequest("competition", "put", {running: true})
    .then(() => alert("The thread was reset"))
    .catch(e => alert(e));
};

const durationButton = document.getElementById("duration-button");
durationButton.onclick = () => {
  const duration = parseInt(document.getElementById("duration-input").value);
  sendAdminRequest("ranked", "put", {duration})
    .then(() => alert("The duration was set"))
    .catch(e => alert(e));
};
