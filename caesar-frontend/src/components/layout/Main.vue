<!-- src/components/Header.vue -->
<template>
    <div class="main">
        <el-main>
            <component :is="getMainComponent()"></component>
        </el-main>
    </div>
</template>


<script>
import TaskManagerMain from '@/components/develop/TaskManagerMain.vue'; // 引入 TaskManagerMain 组件
import SystemManagerMain from '@/components/system/SystemManagerMain.vue'; // 引入 SystemManagerMain 组件

import { EventBus } from '../../common/event-bus';

export default {
    name: 'Main',
    components: {
        TaskManagerMain,
        SystemManagerMain
    },
    data() {
        return {
            loginUser:'admin',
            currentHeaderMenuIndex:"task"
        }
    },
    created() {
        EventBus.$on('login-user', data => {
            this.loginUser = data
        });
        EventBus.$on('header-menu-selected', data => {
            this.currentHeaderMenuIndex = data
        });
    },
    mounted() {
        this.loginUser = sessionStorage.getItem('loginUser');
    },
    methods:{
        getMainComponent(){
            if(this.currentHeaderMenuIndex === "task"){
                return "TaskManagerMain";
            }else if(this.currentHeaderMenuIndex === "system"){
                return "SystemManagerMain";
            }
        }
    }
};
</script>


<style scoped>
.main {
    background-color: white;
    width: 100%;
    height: 100%;
}
</style>
