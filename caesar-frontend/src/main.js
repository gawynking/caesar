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

import axios from 'axios';

axios.defaults.baseURL ="http://localhost:8369";
Vue.prototype.$axios = axios;

Vue.config.productionTip = false;
Vue.use(ElementUI);
Vue.use(Fragment.Plugin)

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
