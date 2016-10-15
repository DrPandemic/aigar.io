var path = require('path');

module.exports = {
  entry: {
    app: './tmp/app.js',
    leaderboardApp: './tmp/leaderboardApp.js'
  },
  output: {
    path: path.join(__dirname, 'dist/web/javascript'),
    filename: '[name].bundle.js',
    chunkFilename: '[id].bundle.js'
  }
};
