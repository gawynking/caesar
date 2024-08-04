<!-- src/components/Header.vue -->
<template>
    <div class="main">
        <el-main>
            <component :is="getMainComponent()"></component>
        </el-main>
    </div>
</template>

<script>
import UserPage from './UserPage.vue';
import UserGroupPage from './UserGroupPage.vue';
import RolePage from './RolePage.vue';
import DatasourcePage from './DatasourcePage.vue';
import DevelopPage from './DevelopPage.vue';
import SchedulePage from './SchedulePage.vue';
import CodeReview from './CodeReview.vue';

import { EventBus } from '../../common/event-bus';

export default {
    name: 'SystemManagerMain',
    components: {
        RolePage,
        UserPage,
        UserGroupPage,
        DatasourcePage,
        DevelopPage,
        SchedulePage,
        CodeReview
    },
    data() {
        return {
            loginUser:'',
            currentSystemAsideMenu : ''
        }
    },
    created() { // tasks-activeTask
        EventBus.$on('tasks-activeTask', data => {
            this.currentSystemAsideMenu = data
        })
    },
    methods:{
        getMainComponent(){
            if(this.currentSystemAsideMenu === "user-manager"){
                return "UserPage";
            }else if(this.currentSystemAsideMenu === "user-group-manager"){
                return "UserGroupPage";
            }else if(this.currentSystemAsideMenu == "role-manager"){
                return "RolePage";
            }else if(this.currentSystemAsideMenu == "datasource-manager"){
                return "DatasourcePage";
            }else if(this.currentSystemAsideMenu == "data-dev-manager"){
                return "DevelopPage";
            }else if(this.currentSystemAsideMenu == "schedule-manager"){
                return "SchedulePage";
            }else if(this.currentSystemAsideMenu == "code-review"){
                return "CodeReview";
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
