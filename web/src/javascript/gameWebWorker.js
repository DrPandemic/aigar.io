import {fetchState} from "./network";
import {networkRefresh, failsBeforeNotExisting} from "./constants";

function getGameId() {
  const querystring = location.search.replace("?", "").split("&");
  const queryObj = {};
  for (let i = 0; i < querystring.length; i++) {
    const name = querystring[i].split("=")[0];
    const value = querystring[i].split("=")[1];
    queryObj[name] = value;
  }

  return parseInt(queryObj["gameId"]) || 0;
}

let started = false;
onmessage = (e) => {
  if(!started) {
    started = true;
    updateLoop(e.data, getGameId);
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
