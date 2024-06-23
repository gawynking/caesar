<!-- src/components/Header.vue -->
<template>
    <div class="main">
        <el-main>
            <!-- 可以在这里添加你需要的内容 -->
            <component :is="getMainComponent()"></component>
            <!-- <TaskManagerMain></TaskManagerMain> -->
        </el-main>
    </div>
</template>

<script>
import TaskManagerMain from '@/components/TaskManagerMain.vue'; // 引入 TaskManagerMain 组件
import SystemManagerMain from '@/components/SystemManagerMain.vue'; // 引入 SystemManagerMain 组件

import { EventBus } from '../common/event-bus';

export default {
    name: 'Main',
    components: {
        TaskManagerMain,
        SystemManagerMain
    },
    data() {
        return {
            currentHeaderMenu:"task"
        }
    },
    created() {
        EventBus.$on('header-menu-selected', data => {
            this.currentHeaderMenu = data
        })
    },
    methods:{
        getMainComponent(){
            if(this.currentHeaderMenu === "task"){
                return "TaskManagerMain";
            }else if(this.currentHeaderMenu === "system"){
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