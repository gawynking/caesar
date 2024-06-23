<!-- src/components/Header.vue -->
<template>
    <div class="aside" style="width: asideWidth;">
        <el-aside :width="asideWidth">
            <el-menu 
                class="aside-el-menu" 
                :collapse="isCollapse" 
                :collapse-transition="false"
                @select="handleSelectTask"
            >

                <div v-show="isVisible">
                    <el-row class="aside-title">
                        <el-col :span="20" class="aside-title-name">
                            <div v-html="setAsideTitle()"></div>
                        </el-col>
                        <el-col :span="4" class="aside-collapse">
                            <i :class="asideCollapseIcon" @click="asideCollapse"></i>
                        </el-col>
                    </el-row>
                    <el-input placeholder="输入筛选内容" v-model="filterText" v-if="isShowFilter()"></el-input>
                </div>

                <div v-show="!isVisible">
                    <el-row class="aside-title">
                        <el-col :span="24" class="aside-collapse">
                            <i :class="asideCollapseIcon" @click="asideCollapse"></i>
                        </el-col>
                    </el-row>
                </div>

                <!-- 可以在这里添加你需要的内容 -->
                <component :is="getAsideComponent()"></component>
                <!-- <TaskManagerAside></TaskManagerAside> -->
                <!-- <SystemManagerAside></SystemManagerAside> -->
            </el-menu>
        </el-aside>
    </div>
</template>

<script>
import TaskManagerAside from '@/components/TaskManagerAside.vue'; // 引入 TaskManagerAside 组件
import SystemManagerAside from '@/components/SystemManagerAside.vue';

import { EventBus } from '../common/event-bus';

export default {
    name: 'Aside',
    components: {
        TaskManagerAside,
        SystemManagerAside
    },
    data() {
        return {
            isCollapse: false,
            asideWidth: "300px",
            asideCollapseIcon: "el-icon-s-fold",
            isVisible: true,

            filterText: "GawynKing.*",
            currentAsideMenu:"task",
            asideListData:[],
            tasks: {
                activeTask:"", // 选中的项 
                selectedTasks: [] // 用于存储选中的项
            }
        }
    },
    created() {
        EventBus.$on('header-menu-selected', data => {
            this.currentAsideMenu = data
        });
        EventBus.$on('asideListData', data => {
            if (typeof data !== 'string') {
                // 如果数据不是字符串，将其转换为字符串
                this.asideListData = JSON.stringify(data);
            } else {
                this.asideListData = data;
            }
        })
    },
    watch: {
        filterText(filterText) {
            this.filterText = filterText;
            EventBus.$emit("filterText",filterText);
        }
    },
    methods: {
        asideCollapse() {
            this.isCollapse = !this.isCollapse
            if (this.isCollapse) {
                this.asideWidth = "64px";
                this.asideCollapseIcon = "el-icon-s-unfold";
                this.isVisible = false;
            } else {
                this.asideWidth = "300px";
                this.asideCollapseIcon = "el-icon-s-fold";
                this.isVisible = true;
            }
        },

        setAsideTitle(){
            if(this.currentAsideMenu === "task"){
                return '任务管理';
            }else if(this.currentAsideMenu === "system"){
                return "系统管理";
            }
        },
        isShowFilter(){
            if(this.currentAsideMenu === "task"){
                return true;
            }else{
                return false;
            }
        },
        getAsideComponent(){
            if(this.currentAsideMenu === "task"){
                return "TaskManagerAside";
            }else if(this.currentAsideMenu === "system"){
                return "SystemManagerAside";
            }
        },
        handleSelectTask(index) {
            this.tasks.activeTask = index;
            if(this.tasks.selectedTasks.includes(index)){

            }else{
                this.tasks.selectedTasks.push(index)
            }
            EventBus.$emit("tasks-activeTask",this.tasks.activeTask);
            EventBus.$emit("tasks-selectedTasks",this.tasks.selectedTasks);
        }
    }
};
</script>

<style scoped>
.aside {
    /* background-color: #ecedf1; */
    height: 100%;
}

.aside-title {
    display: flex;
    width: 100%;
}

.aside-title-name {
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    height: 35px;
    font-size: 20px;
}

.aside-collapse {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 0px;
    height: 35px;
    font-size: 20px;
}
</style>