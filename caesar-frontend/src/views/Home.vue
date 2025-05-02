<!-- src/views/Home.vue -->
<template>
  <div class="home">
    <el-container direction="vertical" class="home-container">
      <Header></Header>
      <el-container class="home-aside-main-container">
        <Aside></Aside>
        <Main></Main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import Header from '@/components/layout/Header.vue'; // 引入 Header 组件
import Footer from '@/components/layout/Footer.vue'; // 引入 Footer 组件
import Aside from '@/components/layout/Aside.vue'; // 引入 Aside 组件
import Main from '@/components/layout/Main.vue'; // 引入 Main 组件

export default {
  name: 'Home',
  components: {
    Header,
    Aside,
    Main,
    Footer
  },
  data(){
    return{
    }
  },
  methods:{
  }
};
</script>

<style>
.el-container{
  height: 100%;
}
.home{
  height: 100%;
}
.home-container{
  height: 100%;
}
.home-aside-main-container{
  height: 100%;
}
</style>