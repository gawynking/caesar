<template>
    <div class="header">
        <el-header height="60px">
            <el-row class="header-row">

                <el-col :span="4">
                    <span class="header-project">
                        CAESAR
                    </span>
                </el-col>

                <el-col :span="20">
                    <div class="header-menu">
                        <el-menu 
                            class="header-menu-item" 
                            mode="horizontal"
                            background-color="#545c64" 
                            text-color="#fff"
                            active-text-color="#ffd04b"
                            :default-active="activeIndex" 
                            @select="handleHeaderMenuSelect" 
                        >
                            <el-menu-item index="task">任务管理</el-menu-item>
                            <el-menu-item index="system">系统管理</el-menu-item>
                            <el-submenu index="user">
                                <template slot="title">{{ loginUser }}</template>
                                <el-menu-item index="exit">退出登录</el-menu-item>
                            </el-submenu>

                        </el-menu>
                    </div>
                </el-col>

            </el-row>
        </el-header>
    </div>
</template>


<script>
import { EventBus } from '../../common/event-bus';

export default {
    name: 'Header',
    data() {
        return {
            loginUser: '',
            activeIndex: 'task', 
            headerMenuList: []
        }
    },
    created() {
        EventBus.$on('login-user', data => {
            this.loginUser = sessionStorage.getItem('loginUser');
        });
    },
    mounted() {
        this.loginUser = sessionStorage.getItem('loginUser');
    },
    watch: {
        loginUser(loginUser) {
            this.loginUser = loginUser;
        }
    },
    methods: {
        handleHeaderMenuSelect(index) {
            this.activeIndex = index;
            EventBus.$emit('header-menu-selected', index);
            if (index === 'exit') {
                this.logout();
            }
        },
        logout() {
            // 清除本地存储中的认证信息
            sessionStorage.removeItem('token');

            // 可选：发送请求到后端通知会话撤销
            this.$axios.post('/auth/logout').then(response => {
                console.log('Logout successful:', response);
            }).catch(error => {
                console.error('Logout failed:', error);
            });

            // 重定向到登录页面
            this.$router.push({ name: 'Login' });
        }
    }
};
</script>


<style scoped>
.header {
    background-color: #545c64;
    width: 100%;
    padding: 0;
}

.header-row {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10px;
}

.header-project {
    width: 200px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: "Helvetica Neue";
    font-size: 30px;
    font-weight: bold;
    border-right: 0px solid #ccc;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.0);
}

.header-menu {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin-right: 0px;
    font-size: 16px;
}

.header-menu-item {
    text-align: right;
    height: 100%;
}
</style>
