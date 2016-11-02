import {fetchState} from "./network";
import {networkRefresh} from "./constants";

async function updateLoop() {
  const startTime = (new Date()).getTime();

  postMessage(await fetchState(0));

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(updateLoop, 1000/networkRefresh - elapsed);
}

updateLoop();
