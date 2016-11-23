import {sendAdminRequest} from "./network";

const seedButton = document.getElementById("seedButton");
seedButton.onclick = () => {
  sendAdminRequest("player", "post", {seed: true})
    .then(() => alert("The DB was seeded"))
    .catch(() => window.location.href = "/web/adminLogin.html");
};

const resetButton = document.getElementById("reset-button");
resetButton.onclick = () => {
  sendAdminRequest("competition", "put", {running: true})
    .then(() => alert("The thread was reset"))
    .catch(e => alert(e));
};
