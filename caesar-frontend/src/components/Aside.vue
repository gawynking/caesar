<!-- src/components/Header.vue -->
<template>
    <div class="aside" style="width: asideWidth;">
        <el-aside :width="asideWidth">
            <el-menu 
                class="aside-el-menu" 
                :collapse="isCollapse" 
                :collapse-transition="false"
                :default-active="activeMenu"
            >

                <div v-show="isVisible">
                    <el-row class="aside-title">
                        <el-col :span="20" class="aside-title-name">
                            <div v-html="setAsideTitle(headerMenuSelected)"></div>
                        </el-col>
                        <el-col :span="4" class="aside-collapse">
                            <i :class="asideCollapseIcon" @click="asideCollapse"></i>
                        </el-col>
                    </el-row>
                    <el-input placeholder="输入筛选内容" v-model="filterText" v-if="isShowFilter(headerMenuSelected)"></el-input>
                </div>

                <div v-show="!isVisible">
                    <el-row class="aside-title">
                        <el-col :span="24" class="aside-collapse">
                            <i :class="asideCollapseIcon" @click="asideCollapse"></i>
                        </el-col>
                    </el-row>
                </div>

                <!-- 可以在这里添加你需要的内容 -->
                <component :is="getAsideComponent(headerMenuSelected)"></component>
                <!-- <TaskManagerAside></TaskManagerAside> -->
                <!-- <SystemManagerAside></SystemManagerAside> -->
            </el-menu>
        </el-aside>
    </div>
</template>

<script>
import TaskManagerAside from '@/components/TaskManagerAside.vue'; // 引入 TaskManagerAside 组件
import SystemManagerAside from '@/components/SystemManagerAside.vue';

export default {
    name: 'Aside',
    components: {
        TaskManagerAside,
        SystemManagerAside
    },
    data() {
        return {
            isCollapse: false,
            activeMenu:"",
            filterText: "GawynKing.*",
            asideWidth: "300px",
            asideCollapseIcon: "el-icon-s-fold",
            isVisible: true,
            currentAsideMenu:"task"
        }
    },
    props:{
        headerMenuSelected: {
            type: String,  
            default: "task",  
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
        setAsideTitle(headerMenuSelected){
            if(headerMenuSelected === "task"){
                return '任务管理';
            }else if(headerMenuSelected === "system"){
                return "系统管理";
            }else{
                if(currentAsideMenu === "task"){
                    return '任务管理';
                }else if(currentAsideMenu === "system"){
                    return "系统管理";
                }
            }
        },
        isShowFilter(headerMenuSelected){
            if(headerMenuSelected === "task"){
                return true;
            }else{
                return false;
            }
        },
        getAsideComponent(headerMenuSelected){
            if(headerMenuSelected === "task"){
                return "TaskManagerAside";
            }else if(headerMenuSelected === "system"){
                return "SystemManagerAside";
            }
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