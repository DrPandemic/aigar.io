const path = require("path");

module.exports = {
  entry: {
    app: "./src/javascript/app.js",
    leaderboardApp: "./src/javascript/leaderboardApp.js"
  },
  output: {
    path: path.join(__dirname, "dist/web/javascript"),
    filename: "[name].bundle.js",
    chunkFilename: "[id].bundle.js"
  },
  devtool: "source-map",
  module: {
    loaders: [
      {
        test: /.js$/,
        loader: "babel-loader",
        exclude: /node_modules/,
        query: {
          plugins: ["transform-runtime", "syntax-async-functions", "transform-regenerator"],
          presets: ["es2015"]
        }
      }
    ]
  },
};
