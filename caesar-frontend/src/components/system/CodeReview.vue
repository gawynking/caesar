<template>
  <div>
    <el-table :data="reviewTasks" style="width: 100%" border stripe highlight-current-row>
      <el-table-column prop="taskName" label="任务名称"></el-table-column>
      <el-table-column prop="version" label="版本号"></el-table-column>
      <el-table-column prop="preVersion" label="上一个版本号"></el-table-column>
      <el-table-column prop="submitUsername" label="提交用户"></el-table-column>
      <el-table-column prop="codeDesc" label="代码描述"></el-table-column>
      <el-table-column prop="reviewStatus" label="审核状态" :formatter="formatReviewStatus"></el-table-column>
      <el-table-column prop="reviewLevel" label="审计阶段" :formatter="formatReviewLevel"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="showReviewDialog(scope.row)">审核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 审核对话框 -->
    <el-dialog title="代码审核" :visible.sync="reviewDialogVisible" width="85%" @close="resetReviewForm">
      <el-row style="margin-bottom: 5px;">
        <el-col :span="13"><span>Version(旧): {{ lastVersion }}</span></el-col>
        <el-col :span="11"><span>Version(新): {{ currentVersion }}</span></el-col>
      </el-row>
      <CodeCompare :oldValue="oldCode" :newValue="newCode" :isReadOnly="true" style="margin-bottom: 30px;"/>
      <el-form ref="reviewForm" :model="reviewForm" label-width="80px">
        <el-form-item label="审核建议" prop="auditMessage">
          <el-input type="textarea" v-model="reviewForm.auditMessage"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="success" @click="submitReview(1)">通过</el-button>
        <el-button type="danger" @click="submitReview(-1)">驳回</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import { EventBus } from '../../common/event-bus';
import CodeCompare from "../../common/CodeCompare.vue"; // 路径代入
export default {
  components: {
        CodeCompare,
    },
  data() {
    return {
      loginUser: "",
      reviewTasks: [], // 代码审核任务数据
      reviewDialogVisible: false, // 审核对话框显示状态
      reviewForm: { // 审核表单数据
        auditMessage: '',
        reviewResult: '',
        id: null,
        taskId: null,
        reviewLevel: null,
        reviewStatus: null
      },
      oldCode:"",
      newCode:"",
      lastVersion:"",
      currentVersion:""
    };
  },
  created() {
    EventBus.$on('login-user', data => {
      this.loginUser = sessionStorage.getItem('loginUser');
    });
  },
  mounted() {
    this.loginUser = sessionStorage.getItem('loginUser');
    // 页面加载时获取代码审核任务列表
    this.fetchReviewTasks();
  },
  methods: {
    // 显示审核对话框
    showReviewDialog(task) {
      this.reviewForm.id = task.id;
      this.reviewForm.taskId = task.taskId;
      this.reviewForm.reviewLevel = task.reviewLevel;
      this.reviewForm.reviewStatus = task.reviewStatus;
      this.reviewDialogVisible = true;

      this.oldCode = task.lastCode;
      this.newCode = task.currentCode;
      this.lastVersion = task.preVersion;
      this.currentVersion = task.version;
    },
    // 重置审核表单
    resetReviewForm() {
      this.$refs.reviewForm.resetFields();
    },
    // 提交审核
    submitReview(result) {
      this.reviewForm.reviewResult = result;

      if (result === 1) {
        this.reviewForm.reviewStatus = 5;
      } else {
        this.reviewForm.reviewStatus = 3;
      }

      this.$axios.get('/review/submit', {
        params: {
          id: this.reviewForm.id,
          taskId: this.reviewForm.taskId,
          reviewLevel: this.reviewForm.reviewLevel,
          reviewStatus: this.reviewForm.reviewStatus,
          reviewResult: this.reviewForm.reviewResult,
          auditMessage: this.reviewForm.auditMessage
        }
      })
        .then(response => {
          this.reviewDialogVisible = false; // 关闭对话框
          this.fetchReviewTasks();
          this.$message.success('审核提交成功');
        })
        .catch(error => {
          console.error('审核提交失败', error);
          this.$message.error('审核提交失败');
        });
    },
    // 获取代码审核任务列表
    fetchReviewTasks() {
      this.$axios.get('/review/getReviewTaskList', {
        params: { loginUser: this.loginUser }
      })
        .then(response => {
          this.reviewTasks = response.data.data.items; // 将获取的审核任务数据填充到页面
        })
        .catch(error => {
          console.error('获取代码审核任务列表失败', error);
          this.$message.error('获取代码审核任务列表失败');
        });
    },
    // 格式化审核状态
    formatReviewStatus(row, column, cellValue) {
      const statusMap = {
        1: '审核中',
        2: '已撤回',
        3: '已驳回',
        4: '系统驳回',
        5: '成功'
      };
      return statusMap[cellValue] || '未知状态';
    },
    formatReviewLevel(row, column, cellValue) {
      const statusMap = {
        1: '初审',
        2: '中审',
        3: '终审'
      };
      return statusMap[cellValue] || '未知状态';
    }
  }
};
</script>

<style scoped>
/* 可以根据需要添加样式 */
</style>
