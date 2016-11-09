module.exports = {
  "env": {
    "browser": true,
    "es6": true
  },
  "extends": "eslint:recommended",
  "parserOptions": {
    "ecmaFeatures": {
      "experimentalObjectRestSpread": true
    },
    "sourceType": "module",
    "ecmaVersion": 8 // or 2017
  },
  "rules": {
    "comma-dangle": [
      "error", "only-multiline"
    ],
    "no-console": [
      "error",
      {
        "allow": ["warn", "error", "log"]
      }
    ],
    "indent": [
      "error",
      2
    ],
    "linebreak-style": [
      "error",
      "unix"
    ],
    "quotes": [
      "error",
      "double"
    ],
    "semi": [
      "error",
      "always"
    ],
    "no-var": [
      "error"
    ]
  }
};
