import {fetchState} from "./network";
import {networkRefresh} from "./constants";

async function updateLoop() {
  postMessage(await fetchState(0));
}

setInterval(updateLoop, 1000/networkRefresh);
