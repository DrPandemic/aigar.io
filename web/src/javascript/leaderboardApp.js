import sort from "immutable-sort";
import he from "he";

import {fetchLeaderboardEntries} from "./network";
import {leaderboardSlices, playerColors} from "./constants";

function displayChart(entries) {
  const players = Array.from(entries.reduce((acc, {player_id}) => acc.add(player_id), new Set()));
  const playerOrder = sort(players, (a, b) => a - b);
  const [min, max] = entries.reduce(
    ([min, max], {timestamp}) => [min < timestamp ? min : timestamp, max > timestamp ? max : timestamp],
    [Date.now(), new Date(0)]
  );
  const debugArray = [];
  const sliceSize = Math.ceil((max - min) / leaderboardSlices);
  const data = entries.reduce((acc, {name, player_id, score, timestamp}) => {
    let accIndex = acc.findIndex(({player_id: val}) => val === player_id);
    if (accIndex === -1) {
      accIndex = acc.length;
      acc.push({name, player_id, scores: []});
    }

    const scoreIndex = Math.floor((timestamp - min) / sliceSize);
    if (player_id === 16) {
      debugArray.push({name, accIndex, scoreIndex, score, timestamp});
    }
    acc[accIndex].scores[scoreIndex] = (acc[accIndex].scores[scoreIndex] || 0) + score;

    return acc;
  }, []).map(entry => {
    for (let i = 0; i < entry.scores.length; ++i) {
      entry.scores[i] = entry.scores[i] || 0;
    }

    let current = 0;
    entry.scores = entry.scores.map(score => {
      score += current;
      current = score;
      return Math.round(score);
    });

    const playerIndex = playerOrder.findIndex(player_id => player_id === entry.player_id);
    return {
      lineTension: 0.1,
      borderColor: playerColors[playerIndex],
      fill: false,
      data: entry.scores,
      label: entry.name,
    };
  }).sort(({data: a}, {data: b}) => b[b.length - 1] - a[a.length - 1]);

  const labels = [];
  for (let i = 0; i < leaderboardSlices; ++i) {
    const date = new Date((min - 0) + ((max - min) / leaderboardSlices * i));
    labels.push(`${date.getHours()}:${date.getMinutes()}`);
  }

  new Chart(document.getElementById("myChart"), {
    type: "line",
    data: {
      datasets: data,
      labels: labels,
    },
    options: {
      animation: {
        duration: 0, // general animation time
      },
      hover: {
        animationDuration: 0, // duration of animations when hovering an item
      },
      responsiveAnimationDuration: 0, // animation duration after a resize
    }
  });
}

function displayEntries(entries) {
  const result = Object.entries(entries.reduce((acc, val) => {
    val = JSON.parse(JSON.stringify(val));
    if (!acc[val.player_id]) {
      acc[val.player_id] = val;
    } else {
      acc[val.player_id].score += val.score;
    }

    return acc;
  }, {})).reduce((acc, [_, val]) => {
    acc.push(val);
    return acc;
  }, []).sort((a, b) => {
    if (a.score < b.score) {
      return -1;
    } else if (a.score > b.score) {
      return 1;
    }
    return 0;
  });

  const leaderboard = document.getElementById("leaderboard-body");
  for(const i in result) {
    const entry = result[i];
    const row = leaderboard.insertRow(0);
    row.insertCell(0).innerHTML = (result.length - parseInt(i)).toString();
    row.insertCell(1).innerHTML = he.encode(entry.name);
    row.insertCell(2).innerHTML = he.encode(entry.score.toFixed(2).toString());
  }
}

function clearEntries() {
  const leaderboard = document.getElementById("leaderboard-body");
  while (leaderboard.firstChild) {
    leaderboard.removeChild(leaderboard.firstChild);
  }
}

function fetchAndDisplay() {
  return fetchLeaderboardEntries()
    .then(({entries, enabled}) => {
      if (enabled) {
        clearEntries();
        displayEntries(entries);
        displayChart(entries);
      } else {
        document.getElementById("disabled-container").style.display = "block";
        document.getElementById("default-container").style.display = "none";
      }
    });
}

setInterval(fetchAndDisplay, 5 * 1000);

fetchAndDisplay();
