const path = require("path");

module.exports = {
  entry: {
    app: "./src/javascript/app.js",
    leaderboardApp: "./src/javascript/leaderboardApp.js",
    gameWebWorker: "./src/javascript/gameWebWorker.js",
    adminApp: "./src/javascript/adminApp.js",
    adminLoginApp: "./src/javascript/adminLoginApp.js",
  },
  output: {
    path: path.join(__dirname, "dist/web/javascript"),
    filename: "[name].bundle.js",
    chunkFilename: "[id].bundle.js"
  },
  devtool: "source-map",
};
