import {fetchState, openGameStateWebsocket} from "./network";
import {networkRefresh, failsBeforeNotExisting} from "./constants";

let started = false;
onmessage = (e) => {
  if(!started) {
    started = true;
    updateLoop(e.data, 0);
  }
};

async function updateLoop(gameId, successCount) {
  const startTime = (new Date()).getTime();

  const result = await fetchState(gameId);

  if(result) {
    successCount = Math.min(failsBeforeNotExisting, successCount + 1);
    postMessage(result);
  } else if(successCount === 0) {
    postMessage(null);
  } else {
    --successCount;
  }

  const elapsed = (new Date()).getTime() - startTime;
  setTimeout(() => updateLoop(gameId, successCount), 1000 / networkRefresh - elapsed);
}

const socket = openGameStateWebsocket(event => {
  console.log("Message from server ", event.data);
});
