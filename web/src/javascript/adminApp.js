import {sendAdminRequest} from "./network";

const seedButton = document.getElementById("seedButton");
seedButton.onclick = () => {
  sendAdminRequest("player", "post", {seed: true})
    .then(() => alert("The DB was seeded"))
    .catch(e => alert(e));
};
