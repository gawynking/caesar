import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue';


Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/caesar',
    name: 'Home',
    component: Home
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = Boolean(sessionStorage.getItem('token')); // 检查本地存储中的 token
  if (to.name !== 'Login' && !isAuthenticated) {
    next({ name: 'Login' }); // 如果没有认证，跳转到登录页
  } else {
    next(); // 否则允许进入目标路由
  }
});

export default router
