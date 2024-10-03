<template>
  <div>
    <el-button type="primary" size="small" @click="showAddDialog">添加数据源</el-button>
    <el-table :data="datasources" style="width: 100%" border stripe highlight-current-row>
      <el-table-column prop="datasourceName" label="数据源名称"></el-table-column>
      <el-table-column prop="datasourceType" label="数据源类型">
        <template slot-scope="scope">
          {{ datasourceTypes[scope.row.datasourceType] }}
        </template>
      </el-table-column>
      <el-table-column prop="engine" label="执行引擎">
        <template slot-scope="scope">
          {{ execEngines[scope.row.engine] }}
        </template>
      </el-table-column>
      <el-table-column prop="datasourceInfo" label="数据源详情">
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" :content="scope.row.datasourceInfo" placement="top">
            <span>{{ scope.row.datasourceInfo }}...</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="danger" size="small" icon="el-icon-delete" @click="confirmDeleteDatasource(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加数据源对话框 -->
    <el-dialog title="添加数据源" :visible.sync="addDialogVisible" width="30%" @close="resetAddForm">
      <el-form ref="addForm" :model="addForm" :rules="rules" label-width="120px">
        <el-form-item label="数据源名称" prop="datasourceName">
          <el-input v-model="addForm.datasourceName" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="数据源类型" prop="datasourceType">
          <el-select v-model="addForm.datasourceType" placeholder="请选择">
            <el-option v-for="(label, value) in datasourceTypes" :key="value" :label="label" :value="value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="执行引擎" prop="engine">
          <el-select v-model="addForm.engine" placeholder="请选择">
            <el-option v-for="(label, value) in execEngines" :key="value" :label="label" :value="value"></el-option>
          </el-select>
        </el-form-item>

        <!-- 显示Doris相关表单项 -->
        <el-form-item v-if="isJdbc" label="URL" prop="url">
          <el-input v-model="addForm.url" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item v-if="isJdbc" label="用户名" prop="username">
          <el-input v-model="addForm.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item v-if="isJdbc" label="密码" prop="password">
          <el-input type="password" v-model="addForm.password" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item v-if="isJdbc" label="默认数据库" prop="dbName">
          <el-input v-model="addForm.dbName" autocomplete="off"></el-input>
        </el-form-item>

        <!-- 显示Spark/Flink相关表单项 -->
        <el-form-item v-if="!isJdbc" label="客户端地址" prop="ipAddr">
          <el-input v-model="addForm.ipAddr" autocomplete="off"></el-input>
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
      loginUser: '',
      datasources: [], // 数据源数据
      addDialogVisible: false, // 添加数据源对话框显示状态
      addForm: { // 添加数据源表单数据
        datasourceName: '',
        datasourceType: '',
        engine: '',

        // jdbc 选项 
        url: '',
        username: '',
        password: '',
        dbName: '',

        // 其他选项 
        ipAddr: ''
      },
      datasourceTypes: {
        1: '测试',
        3: '生产'
      },
      execEngines: {
        102: 'Spark',
        103: 'Flink',
        301: 'Doris',
      },
      rules: {
        datasourceName: [
          { required: true, message: '请输入数据源名称', trigger: 'blur' }
        ],
        datasourceType: [
          { required: true, message: '请选择数据源类型', trigger: 'change' }
        ],
        engine: [
          { required: true, message: '请选择执行引擎', trigger: 'change' }
        ],
        url: [
          { required: false, message: '请输入数据源URL', trigger: 'blur' }
        ],
        username: [
          { required: false, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: false, message: '请输入密码', trigger: 'blur' }
        ],
        dbName: [
          { required: false, message: '请输入默认数据库', trigger: 'blur' }
        ],
        ipAddr: [
          { required: false, message: '请输入客户端地址', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    // 计算属性，用来判断是否选择了Doris引擎
    isJdbc() {
      return this.addForm.engine === '301'; // 301 是 Doris 的引擎标识
    }
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
          var datasourceInfo = '{}';
          if(this.isJdbc){
              datasourceInfo = JSON.stringify({
                url: this.addForm.url,
                username: this.addForm.username,
                password: this.addForm.password,
                dbName: this.addForm.dbName
              });
          }else {
            datasourceInfo = JSON.stringify({
              ipAddr : this.addForm.ipAddr
            })
          }
          
          this.loginUser = sessionStorage.getItem('loginUser');
          const datasourceData = {
            datasourceName: this.addForm.datasourceName,
            datasourceType: this.addForm.datasourceType,
            engine: this.addForm.engine,
            datasourceInfo,
            ownerName : this.loginUser 
          };

          this.$axios.post('/datasource/addDatasource', datasourceData)
            .then(response => {
              this.datasources.push(response.data);
              this.addDialogVisible = false;
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
    deleteDatasource(id) {
      this.$axios.get('/datasource/deleteDatasource', { params: { id: id } })
        .then(response => {
          this.datasources = this.datasources.filter(datasource => datasource.id !== id);
          this.$message.success('删除数据源成功');
          this.fetchDatasources();
        })
        .catch(error => {
          console.error('删除数据源失败', error);
          this.$message.error('删除数据源失败');
        });
    },
    fetchDatasources() {
      this.$axios.get('/datasource/getDatasourceList')
        .then(response => {
          const items = response.data?.data?.items || [];

          if (Array.isArray(items)) {
            this.datasources = items.map(datasource => {
              // 解析 datasourceInfo JSON 字符串为对象
              let datasourceInfoObj = {};
              try {
                datasourceInfoObj = JSON.parse(datasource.datasourceInfo);
              } catch (error) {
                console.error('解析 datasourceInfo 失败', error);
                this.$message.error('解析数据源信息失败');
              }

              // 删除 password 属性
              delete datasourceInfoObj.password;

              // 将修改后的对象重新转为 JSON 字符串
              const updatedDatasourceInfo = JSON.stringify(datasourceInfoObj);

              // 返回新的数据源对象
              return {
                ...datasource,
                datasourceInfo: updatedDatasourceInfo // 将删除了 password 后的字符串重新赋值
              };
            });
          } else {
            this.$message.error('数据格式不正确，期望 items 是数组');
          }
        })
        .catch(error => {
          console.error('获取数据源列表失败', error);
          this.$message.error('获取数据源列表失败');
        });
    }
  },
  mounted() {
    this.loginUser = sessionStorage.getItem('loginUser');
    this.fetchDatasources();
  }
};
</script>

<style scoped>
/* 可以根据需要添加样式 */
</style>
