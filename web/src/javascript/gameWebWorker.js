import {fetchState} from "./network";
import {networkRefresh} from "./constants";

async function updateLoop() {
  const startTime = (new Date()).getTime();

  const result = await fetchState(0);
  if(result) {
    postMessage(result);
  }

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(updateLoop, 1000/networkRefresh - elapsed);
}

updateLoop();
