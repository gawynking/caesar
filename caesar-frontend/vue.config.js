const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');

module.exports = {
  configureWebpack: {
    plugins: [
      new MonacoWebpackPlugin({
        // 只包含你需要的语言和功能
        languages: ['sql', 'javascript', 'python', 'shell', 'powershell', 'php'],
        features: [
          'coreCommands',
          'find',
          'comment',
          'suggest',
          'multicursor',
          'bracketMatching'
        ]
      })
    ]
  },
  chainWebpack: config => {
    // 解决 Monaco Editor 的 loader 问题
    config.module
      .rule('monaco-editor')
      .test(/\.ttf$/)
      .use('file-loader')
      .loader('file-loader')
      .end();
  },
  devServer: {
    proxy: {
      '/caesar': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/caesar': '' // 将 /caesar 前缀去掉
        }
      }
    }
  }
};