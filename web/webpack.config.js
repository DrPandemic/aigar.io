var path = require('path');

module.exports = {
	entry: './tmp/app.js',
	output: {
		path: path.join(__dirname, 'dist/web/javascript'),
		filename: '[name].bundle.js',
		chunkFilename: '[id].bundle.js'
	}
};
