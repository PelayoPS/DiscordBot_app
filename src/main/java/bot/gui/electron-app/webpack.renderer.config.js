const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const rules = require('./webpack.rules');

rules.push({
  test: /\.css$/,
  use: [{ loader: 'style-loader' }, { loader: 'css-loader' }],
});

module.exports = {
  module: {
    rules,
  },
  plugins: [
    new CopyWebpackPlugin({
      patterns: [
        { from: 'src/index.html', to: '../main/src/index.html' },
        { from: 'src/styles.css', to: '../main/src/styles.css' },
        { from: 'src/scripts.js', to: '../main/src/scripts.js' },
        { from: 'src/js', to: '../main/src/js' },
      ],
    }),
  ],
};
