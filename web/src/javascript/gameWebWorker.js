import {fetchState} from "./network";

async function updateLoop() {
  postMessage(await fetchState(0));
}

setInterval(updateLoop, 1000/24);
