const apiURL = "http://localhost:1337/api/1/game/";

export function fetchState(gameId) {
  return fetch(`${apiURL}${gameId}`, {method: "get"})
    .then(response => {
      return response.json();
    })
    .then(response => {
      return response.data;
    });
}
