<!-- src/components/SystemManagerAside.vue -->
<template>
    <fragment class="system-manager-aside">

        <!-- <el-menu-item index="role-manager" disabled>
            <i class="el-icon-menu"></i>
            <span slot="title">角色管理</span>
        </el-menu-item> -->
        
        <el-menu-item index="user-group-manager">
            <i class="el-icon-menu"></i>
            <span slot="title">用户组管理</span>
        </el-menu-item>
        <el-menu-item index="user-manager">
            <i class="el-icon-menu"></i>
            <span slot="title">用户管理</span>
        </el-menu-item>
        <el-menu-item index="code-review">
            <i class="el-icon-menu"></i>
            <span slot="title">代码审核</span>
        </el-menu-item>

        <el-submenu index="env-manager">
            <template slot="title">
                <i class="el-icon-menu"></i>
                <span slot="title">环境管理</span>
            </template>

            <el-menu-item index="datasource-manager">
                <i class="el-icon-menu"></i>
                <span slot="title">数据源管理</span>
            </el-menu-item>

            <!-- <el-menu-item index="data-dev-manager" disabled>
                <i class="el-icon-menu"></i>
                <span slot="title">数据开发环境管理</span>
            </el-menu-item> -->

            <el-menu-item index="schedule-manager">
                <i class="el-icon-menu"></i>
                <span slot="title">调度配置</span>
            </el-menu-item>
        </el-submenu>
        
    </fragment>
</template>

<script>
export default {
    name: 'SystemManagerAside',
    data() {
        return {
        }
    },
    methods: {
    }
};
</script>

<style scoped>
</style>
