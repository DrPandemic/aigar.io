import {openGameStateWebsocket} from "./network";

const socket = openGameStateWebsocket(event => {
  const data = JSON.parse(event.data);
  postMessage(data);
});
