<!-- src/components/Header.vue -->
<template>
    <div class="main">
        <el-main>
            <!-- 可以在这里添加你需要的内容 -->
            <component :is="getMainComponent(headerMenuSelected)"></component>
            <!-- <TaskManagerMain></TaskManagerMain> -->
        </el-main>
    </div>
</template>

<script>
import TaskManagerMain from '@/components/TaskManagerMain.vue'; // 引入 TaskManagerMain 组件

export default {
    name: 'Main',
    components: {
        TaskManagerMain
    },
    props:{
        headerMenuSelected: {
            type: String,  
            default: "task",  
        }
    },
    methods:{
        getMainComponent(headerMenuSelected){
            if(headerMenuSelected === "task"){
                return "TaskManagerMain";
            }else if(headerMenuSelected === "system"){
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