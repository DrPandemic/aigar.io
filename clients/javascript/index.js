const argv = require('minimist')(process.argv.slice(2));
const UpdatesPerSecond = 3;  // how many times we should contact the server per second

function readConfigFile() {
  const defaultConfigFileName = 'player.default.json';
  const defaultConfigValue = 'REPLACEME';
  const configFileName = 'player.json';

  try {
    const configFile = require(configFileName);

    return {};
  } catch(e) {
    throw `
ERROR: Could not find '${configFileName}'.
Did you forget to rename '${defaultConfigFileName}' to '${configFileName}'?
`;
  }
}

function readArguments(params) {
  return Object.assign({}, params, {
    create: argv['create'] || false,
    join: argv['join'] || false
  });
}

function main(params) {
  console.log(params);
}

Promise.resolve()
  .then(readConfigFile)
  .then(readArguments)
  .then(main)
  .catch(error => console.error(error));
