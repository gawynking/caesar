// main.js 
// 引入VUE依赖 
import Vue from 'vue';
import App from './App.vue';
import router from './router';

// 引入element-ui依赖 
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

// 引入全局的CSS依赖
import './assets/global.css';

// 引入vue-fragment依赖,解决左侧菜单栏折叠后文字不消失问题
import Fragment from 'vue-fragment'

// 处理后端请求
import axios from 'axios';

// 请求拦截器
axios.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
axios.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response && error.response.status === 401) {
      // 处理 401 错误，例如重定向到登录页面
      console.error('Authentication error, please login again.');
      // 可以根据具体情况进行重定向等操作
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

axios.defaults.baseURL ="http://localhost:8369/caesar";
Vue.prototype.$axios = axios;

Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.use(Fragment.Plugin)
Vue.use(router)

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
