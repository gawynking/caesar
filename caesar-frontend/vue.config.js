module.exports = {
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
