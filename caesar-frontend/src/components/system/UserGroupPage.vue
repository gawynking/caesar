<template>
    <div>
        <el-button type="primary" size="small" @click="showAddDialog">添加用户组</el-button>
        <el-table
            :data="groups"
            style="width: 100%"
            border
            stripe
            highlight-current-row
        >
            <el-table-column prop="groupName" label="组名称"></el-table-column>
            <el-table-column prop="groupDesc" label="组描述"></el-table-column>
            <el-table-column prop="ownerName" label="组负责人"></el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button type="danger" size="small" icon="el-icon-delete" @click="confirmDeleteGroup(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 添加用户组对话框 -->
        <el-dialog
            title="添加用户组"
            :visible.sync="addDialogVisible"
            width="30%"
            @close="resetAddForm"
        >
            <el-form ref="addForm" :model="addForm" label-width="80px">
                <el-form-item label="组名称" prop="groupName">
                    <el-input v-model="addForm.groupName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="组描述" prop="groupDesc">
                    <el-input v-model="addForm.groupDesc" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="组负责人" prop="ownerId">
                    <el-select v-model="addForm.ownerId" placeholder="请选择">
                        <el-option
                            v-for="user in userList"
                            :key="user.id"
                            :label="user.username"
                            :value="user.id"
                        ></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="addDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="addGroup">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    data() {
        return {
            userList: [], // 用户列表
            groups: [], // 用户组数据
            addDialogVisible: false, // 添加用户组对话框显示状态
            addForm: { // 添加用户组表单数据
                groupName: '',
                groupDesc: '',
                ownerId: ''
            }
        };
    },
    methods: {
        // 显示添加用户组对话框
        showAddDialog() {
            this.addDialogVisible = true;
        },
        // 重置添加用户组表单
        resetAddForm() {
            this.$refs.addForm.resetFields();
        },
        // 添加用户组
        addGroup() {
            // 发送添加用户组请求给后端，假设使用axios库发送请求
            this.$axios.post('/team/addTeamGroup', this.addForm)
                .then(response => {
                    this.groups.push(response.data); // 添加成功后更新页面数据
                    this.addDialogVisible = false; // 关闭对话框
                    this.fetchGroups(); // 重新获取用户组列表
                    this.$message.success('添加用户组成功');
                })
                .catch(error => {
                    console.error('添加用户组失败', error);
                    this.$message.error('添加用户组失败');
                });
        },
        // 确认删除用户组
        confirmDeleteGroup(id) {
            this.$confirm('此操作将永久删除该用户组, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deleteGroup(id);
            }).catch(() => {
                this.$message.info('已取消删除');
            });
        },
        // 删除用户组
        deleteGroup(id) {
            console.log("aaaaaaa" + id)
            // 发送删除用户组请求给后端，假设使用axios库发送请求
            this.$axios.get('/team/deleteTeamGroup', {
                params: { id: id }
            })
                .then(response => {
                    this.groups = this.groups.filter(group => group.id !== id); // 删除成功后更新页面数据
                    this.$message.success('删除用户组成功');
                })
                .catch(error => {
                    console.error('删除用户组失败', error);
                    this.$message.error('删除用户组失败');
                });
        },
        // 获取用户组列表
        fetchGroups() {
            this.$axios.get('/team/getTeamList')
                .then(response => {
                    this.groups = response.data.data.items; // 将获取的用户组数据填充到页面
                })
                .catch(error => {
                    console.error('获取用户组列表失败', error);
                    this.$message.error('获取用户组列表失败');
                });
        },
        // 获取用户列表
        async getUserList() {
            try {
                const response = await this.$axios.get('/user/getUserList');
                if (response.data.code === 200) {
                    this.userList = response.data.data.items;
                } else {
                    this.$message.error('获取用户列表失败');
                }
            } catch (error) {
                console.error('获取用户列表失败:', error);
                this.$message.error('获取用户列表失败');
            }
        }
    },
    mounted() {
        // 页面加载时获取用户组列表和用户列表
        this.fetchGroups();
        this.getUserList();
    }
};
</script>

<style scoped>
/* 可以根据需要添加样式 */
</style>
