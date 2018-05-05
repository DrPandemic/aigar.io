import {LeaderboardEntry} from "./leaderboardEntry";
import {
  adminApiURL,
  leaderboardApiURL,
  stateApiURL,
  debug,
} from "./constants";

export function fetchState(gameId) {
  return fetch(`${stateApiURL}${gameId}`, {method: "get"})
    .then(response => {
      return response.json();
    })
    .then(response => {
      return response.data;
    })
    .catch(error => {
      if(debug) console.error(error);
    });
}

export function fetchLeaderboardEntries() {
  return fetch(leaderboardApiURL, {method: "get"})
    .then(response => {
      return response.json();
    }).then(response => {
      return response.data.map(entry => new LeaderboardEntry(
        entry.player_id,
        entry.name,
        entry.score)).sort((a, b) => a.score - b.score);
    })
    .catch(error => {
      if(debug) console.error(error);
    });
}

export function sendAdminRequest(url, method, data = {}) {
  data.administrator_password = localStorage.getItem("adminPassword");

  return fetch(adminApiURL + url, {
    method,
    headers: new Headers({
      "Content-Type": "application/json",
    }),
    body: JSON.stringify(data),
  }).then(response => {
    if (response.status === 403) {
      window.location.href = "/web/adminLogin.html";
      throw "Access denied";
    }
    return response;
  }).then(response => response.json())
    .then(response => {
      if (!response.data) throw "The server didn't return a success";
      else return response.data;
    });
}

export function openGameStateWebsocket(onMessage) {
  const socket = new WebSocket(`${self.location.origin}/websocket/1`.replace(/^https/, "wss"));
  socket.addEventListener("message", onMessage);
  return socket;
}
