<!-- src/components/SystemManagerAside.vue -->
<template>
    <fragment class="system-manager-aside">
        <!-- 管理员专属菜单 -->
        <template v-if="isAdmin">
            <el-menu-item index="user-group-manager">
                <i class="el-icon-menu"></i>
                <span slot="title">用户组管理</span>
            </el-menu-item>
            <el-menu-item index="user-manager">
                <i class="el-icon-menu"></i>
                <span slot="title">用户管理</span>
            </el-menu-item>
        </template>

        <!-- 所有用户可见菜单 -->
        <el-menu-item index="code-review">
            <i class="el-icon-menu"></i>
            <span slot="title">代码审核</span>
        </el-menu-item>
    </fragment>
</template>

<script>
import { EventBus } from '../../common/event-bus';

export default {
    name: 'SystemManagerAside',
    data() {
        return {
            loginUser: '',
            isAdmin: false 
        }
    },
    mounted() {
        this.loginUser = sessionStorage.getItem('loginUser');
        
        // 初始化时检查管理员状态
        this.checkAdminStatus();
    },
    methods: {
        async checkAdminStatus() {
            try {
                const response = await this.$axios.get("/user/validAdminUser",{
                    params: { userName: this.loginUser }
                });
                console.log('------- ' + JSON.stringify(response))
                if (response.data && response.data.status === 'success') {
                    // 确保将字符串转换为布尔值
                    this.isAdmin = response.data.data.items === 'true' || response.data.data.items === true;
                } else {
                    this.isAdmin = false;
                }
            } catch (error) {
                console.error("Failed to validate admin user:", error);
                this.isAdmin = false;
            }
        }
    }
};
</script>

<style scoped>
</style>