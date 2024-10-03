<template>
    <div>
      <el-button type="primary" @click="openAddDialog">添加调度集群</el-button>
      <el-table :data="clusterList" style="width: 100%" border>
        <el-table-column prop="ipAddr" label="IP地址"></el-table-column>
        <el-table-column prop="scheduleCategory" label="调度类别">
          <template slot-scope="scope">
            <span v-if="scope.row.scheduleCategory === 1">DolphinScheduler</span>
            <span v-if="scope.row.scheduleCategory === 2">Hera</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button @click="openEditDialog(scope.row)" type="primary" size="small">编辑</el-button>
            <el-button @click="deleteCluster(scope.row.id)" type="danger" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 添加/编辑对话框 -->
      <el-dialog :title="isEdit ? '编辑调度集群' : '添加调度集群'" :visible.sync="dialogVisible">
        <el-form :model="clusterForm">
          <el-form-item label="IP地址">
            <el-input v-model="clusterForm.ipAddr"></el-input>
          </el-form-item>
          <el-form-item label="调度类别">
            <el-select v-model="clusterForm.scheduleCategory" placeholder="选择调度类别">
              <el-option label="DolphinScheduler" :value="1"></el-option>
              <el-option label="Hera" :value="2"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="isEdit ? updateCluster() : addCluster()">确定</el-button>
        </div>
      </el-dialog>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        clusterList: [], // 表数据
        dialogVisible: false, // 控制对话框的显示
        isEdit: false, // 区分是添加还是编辑
        clusterForm: {
          id: '',
          ipAddr: '',
          scheduleCategory: '',
        },
      };
    },
    methods: {
      // 获取表数据
      fetchClusterList() {
        this.$axios.get('/scheduler/getScheduleClusters').then((response) => {
          this.clusterList = response.data.data.items;
        });
      },
  
      // 打开添加对话框
      openAddDialog() {
        this.isEdit = false;
        this.resetForm();
        this.dialogVisible = true;
      },
  
      // 打开编辑对话框
      openEditDialog(row) {
        this.isEdit = true;
        this.clusterForm = { ...row };
        this.dialogVisible = true;
      },
  
      // 重置表单
      resetForm() {
        this.clusterForm = {
          id: '',
          ipAddr: '',
          scheduleCategory: '',
        };
      },
  
      // 添加调度集群
      addCluster() {
        this.$axios.post('/scheduler/addScheduleCluster', this.clusterForm).then(() => {
          this.dialogVisible = false;
          this.fetchClusterList(); // 重新获取表数据
        });
      },
  
      // 更新调度集群
      updateCluster() {
        this.$axios.post('/scheduler/updateScheduleCluster', this.clusterForm).then(() => {
          this.dialogVisible = false;
          this.fetchClusterList(); // 重新获取表数据
        });
      },
  
      // 删除调度集群
      deleteCluster(id) {
        this.$confirm('确认删除该集群吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          this.$axios.get('/scheduler/deleteScheduleCluster',{
            params: {
                id : id 
            }
        }).then(() => {
            this.fetchClusterList(); // 重新获取表数据
          });
        });
      },
    },
    mounted() {
      this.fetchClusterList(); // 页面加载时获取表数据
    },
  };
  </script>
  