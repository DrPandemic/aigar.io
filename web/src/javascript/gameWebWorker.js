import {fetchState} from "./network";
import {networkRefresh} from "./constants";

let started = false;
onmessage = (e) => {
  if(!started) {
    started = true;
    updateLoop(e.data);
  }
};

async function updateLoop(gameId) {
  const startTime = (new Date()).getTime();

  const result = await fetchState(gameId);
  if(result) {
    postMessage(result);
  }

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(() => updateLoop(gameId), 1000/networkRefresh - elapsed);
}

