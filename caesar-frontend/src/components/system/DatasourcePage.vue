<template>
    <div>
      <el-button type="primary" size="small" @click="showAddDialog">添加数据源</el-button>
      <el-table
        :data="datasources"
        style="width: 100%"
        border
        stripe
        highlight-current-row
      >
        <el-table-column prop="datasourceName" label="数据源名称"></el-table-column>
        <el-table-column prop="datasourceType" label="数据源类型">
          <template slot-scope="scope">
            {{ datasourceTypes[scope.row.datasourceType] }}
          </template>
        </el-table-column>
        <el-table-column prop="execEngine" label="执行引擎">
          <template slot-scope="scope">
            {{ execEngines[scope.row.execEngine] }}
          </template>
        </el-table-column>
        <el-table-column prop="url" label="URL"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="dbName" label="默认数据库"></el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="danger" size="small" icon="el-icon-delete" @click="confirmDeleteDatasource(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 添加数据源对话框 -->
      <el-dialog
        title="添加数据源"
        :visible.sync="addDialogVisible"
        width="30%"
        @close="resetAddForm"
      >
        <el-form ref="addForm" :model="addForm" :rules="rules" label-width="120px">
          <el-form-item label="数据源名称" prop="datasourceName">
            <el-input v-model="addForm.datasourceName" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="数据源类型" prop="datasourceType">
            <el-select v-model="addForm.datasourceType" placeholder="请选择">
              <el-option v-for="(label, value) in datasourceTypes" :key="value" :label="label" :value="value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="执行引擎" prop="execEngine">
            <el-select v-model="addForm.execEngine" placeholder="请选择">
              <el-option v-for="(label, value) in execEngines" :key="value" :label="label" :value="value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="URL" prop="url">
            <el-input v-model="addForm.url" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="addForm.username" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="addForm.password" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="默认数据库" prop="dbName">
            <el-input v-model="addForm.dbName" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addDatasource">确定</el-button>
        </span>
      </el-dialog>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        datasources: [], // 数据源数据
        addDialogVisible: false, // 添加数据源对话框显示状态
        addForm: { // 添加数据源表单数据
          datasourceName: '',
          datasourceType: '',
          execEngine: '',
          url: '',
          username: '',
          password: '',
          dbName: ''
        },
        datasourceTypes: {
          1: '测试',
          2: '预发',
          3: '生产'
        },
        execEngines: {
          1: 'Hive',
          2: 'Spark',
          3: 'Flink',
          4: 'Doris',
          5: 'MySQL',
          6: 'Hbase'
        },
        rules: {
          datasourceName: [
            { required: true, message: '请输入数据源名称', trigger: 'blur' }
          ],
          datasourceType: [
            { required: true, message: '请选择数据源类型', trigger: 'change' }
          ],
          execEngine: [
            { required: true, message: '请选择执行引擎', trigger: 'change' }
          ],
          url: [
            { required: true, message: '请输入数据源URL', trigger: 'blur' }
          ],
          username: [
            { required: true, message: '请输入用户名', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' }
          ],
          dbName: [
            { required: true, message: '请输入默认数据库', trigger: 'blur' }
          ]
        }
      };
    },
    methods: {
      // 显示添加数据源对话框
      showAddDialog() {
        this.addDialogVisible = true;
      },
      // 重置添加数据源表单
      resetAddForm() {
        this.$refs.addForm.resetFields();
      },
      // 添加数据源
      addDatasource() {
        this.$refs.addForm.validate((valid) => {
          if (valid) {
            // 发送添加数据源请求给后端，这里假设使用axios库发送请求
            this.$axios.post('/datasource/addDatasource', this.addForm)
              .then(response => {
                this.datasources.push(response.data); // 添加成功后更新页面数据
                this.addDialogVisible = false; // 关闭对话框
                this.fetchDatasources();
                this.$message.success('添加数据源成功');
              })
              .catch(error => {
                console.error('添加数据源失败', error);
                this.$message.error('添加数据源失败');
              });
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      // 确认删除数据源
      confirmDeleteDatasource(id) {
        this.$confirm('此操作将永久删除该数据源, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteDatasource(id);
        }).catch(() => {
          this.$message.info('已取消删除');
        });
      },
      // 删除数据源
      deleteDatasource(id) {
        // 发送删除数据源请求给后端，这里假设使用axios库发送请求
        this.$axios.get('/datasource/deleteDatasource', {
          params: { id: id }
        })
          .then(response => {
            this.datasources = this.datasources.filter(datasource => datasource.id !== id); // 删除成功后更新页面数据
            this.$message.success('删除数据源成功');
            this.fetchDatasources();
          })
          .catch(error => {
            console.error('删除数据源失败', error);
            this.$message.error('删除数据源失败');
          });
      },
      // 获取数据源列表
      fetchDatasources() {
        this.$axios.get('/datasource/getDatasourceList')
          .then(response => {
            this.datasources = response.data.data.items; // 将获取的数据源数据填充到页面
          })
          .catch(error => {
            console.error('获取数据源列表失败', error);
            this.$message.error('获取数据源列表失败');
          });
      }
    },
    mounted() {
      // 页面加载时获取数据源列表
      this.fetchDatasources();
    }
  };
  </script>
  
  <style scoped>
  /* 可以根据需要添加样式 */
  </style>
