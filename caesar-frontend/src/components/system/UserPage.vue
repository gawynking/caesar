<template>
    <div>
      <el-button type="primary" size="small" @click="showAddDialog">添加用户</el-button>
      <el-table
        :data="users"
        style="width: 100%"
        border
        stripe
        highlight-current-row
      >
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <!-- <el-table-column prop="groups" label="所属组"></el-table-column> -->
        <el-table-column label="所属组">
          <template slot-scope="scope">  
            <span v-for="(group, index) in scope.row.groups" :key="index">  
              {{ group.groupName }}
              <span v-if="index < scope.row.groups.length - 1">, </span>  
            </span>  
          </template>  
        </el-table-column>
        <el-table-column prop="isActivated" label="是否激活">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isActivated ? 'success' : 'info'">
              {{ scope.row.isActivated ? '激活' : '未激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="success" size="small" @click="activateUser(scope.row.id)" v-if="!scope.row.isEffective">激活</el-button>
            <el-button type="danger" size="small" icon="el-icon-delete" @click="confirmDeleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 添加用户对话框 -->
      <el-dialog
        title="添加用户"
        :visible.sync="addDialogVisible"
        width="30%"
        @close="resetAddForm"
      >
        <el-form ref="addForm" :model="addForm" :rules="rules" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="addForm.username" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="addForm.password" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="addForm.email" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="电话" prop="phone">
            <el-input v-model="addForm.phone" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="所属组" prop="teamGroup">
            <el-select v-model="addForm.teamGroup" placeholder="请选择">
              <el-option
                v-for="item in teamList"
                :key="item.id"
                :label="item.groupName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addUser">确定</el-button>
        </span>
      </el-dialog>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        teamList: [], // 团队列表
        users: [], // 用户数据
        addDialogVisible: false, // 添加用户对话框显示状态
        addForm: { // 添加用户表单数据
          username: '',
          password: '',
          email: '',
          phone: '',
          teamGroup: ''
        },
        rules: {
          username: [
            { required: true, message: '请输入用户名', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' }
          ],
          email: [
            { required: true, message: '请输入邮箱地址', trigger: 'blur' },
            { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
          ],
          phone: [
            { required: true, message: '请输入手机号', trigger: 'blur' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }
          ],
          teamGroup: [
            { required: true, message: '请选择所属团队', trigger: 'blur' }
          ]
        }
      };
    },
    methods: {
      // 显示添加用户对话框
      showAddDialog() {
        this.addDialogVisible = true;
      },
      // 重置添加用户表单
      resetAddForm() {
        this.$refs.addForm.resetFields();
      },
      // 添加用户
      addUser() {
        this.$refs.addForm.validate((valid) => {
          if (valid) {
            // 发送添加用户请求给后端，这里假设使用axios库发送请求
            this.$axios.post('/user/addUser', this.addForm)
              .then(response => {
                this.users.push(response.data); // 添加成功后更新页面数据
                this.addDialogVisible = false; // 关闭对话框
                this.fetchUsers();
                this.$message.success('添加用户成功');
              })
              .catch(error => {
                console.error('添加用户失败', error);
                this.$message.error('添加用户失败');
              });
          } else {
            this.$message.error('表单填写有误，请检查');
            return false;
          }
        });
      },
      // 激活用户
      activateUser(id) {
        // 发送激活用户请求给后端，这里假设使用axios库发送请求
        this.$axios.get('/user/activatedUser', {
            params: { id: id }
          })
          .then(response => {
            this.fetchUsers();
            this.$message.success('用户激活成功');
          })
          .catch(error => {
            console.error('用户激活失败', error);
            this.$message.error('用户激活失败');
          });
      },
      // 确认删除用户
      confirmDeleteUser(id) {
        this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteUser(id);
        }).catch(() => {
          this.$message.info('已取消删除');
        });
      },
      // 删除用户
      deleteUser(id) {
        // 发送删除用户请求给后端，这里假设使用axios库发送请求
        this.$axios.get('/user/deleteUser', {
            params: { id: id }
          })
          .then(response => {
            this.users = this.users.filter(user => user.id !== id); // 删除成功后更新页面数据
            this.$message.success('删除用户成功');
            this.fetchUsers();
          })
          .catch(error => {
            console.error('删除用户失败', error);
            this.$message.error('删除用户失败');
          });
      },
      // 获取用户列表
      fetchUsers() {
        this.$axios.get('/user/getUserList')
          .then(response => {
            this.users = response.data.data.items; // 将获取的用户数据填充到页面
          })
          .catch(error => {
            console.error('获取用户列表失败', error);
            this.$message.error('获取用户列表失败');
          });
      },
      // 获取团队列表
      async getTeamList() {
        try {
          const response = await this.$axios.get('/auth/getTeamList');
          if (response.data.code === 200) {
            this.teamList = response.data.data.items;
          } else {
            this.$message.error('获取团队列表失败');
          }
        } catch (error) {
          console.error('获取团队列表失败:', error);
          this.$message.error('获取团队列表失败');
        }
      }
    },
    mounted() {
      // 页面加载时获取用户列表和团队列表
      this.fetchUsers();
      this.getTeamList();
    }
  };
  </script>
  
  <style scoped>
  /* 可以根据需要添加样式 */
  </style>
  