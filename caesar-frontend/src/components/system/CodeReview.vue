<template>
  <div>
    <!-- 代码审核列表（仅管理员可见） -->
    <el-card v-if="isAdmin" class="box-card" style="margin-bottom: 20px;">
      <div slot="header" class="clearfix">
        <span>代码审核列表</span>
        <el-form :inline="true" :model="reviewQueryForm" class="demo-form-inline" style="float: right;">
          <el-form-item label="任务名称">
            <el-input v-model="reviewQueryForm.taskName" placeholder="任务名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="reviewQueryForm.reviewStatus" placeholder="审核状态" clearable>
              <el-option label="全部" value=""></el-option>
              <el-option label="审核中" value="1"></el-option>
              <el-option label="已撤回" value="2"></el-option>
              <el-option label="已驳回" value="3"></el-option>
              <el-option label="系统驳回" value="4"></el-option>
              <el-option label="成功" value="5"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="审计阶段">
            <el-select v-model="reviewQueryForm.reviewLevel" placeholder="审计阶段" clearable>
              <el-option label="全部" value=""></el-option>
              <el-option label="初审" value="1"></el-option>
              <el-option label="中审" value="2"></el-option>
              <el-option label="终审" value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchReviewTasks">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
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
    </el-card>

    <!-- 测试验证列表（所有用户可见） -->
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>测试验证列表</span>
        <el-form :inline="true" :model="testQueryForm" class="demo-form-inline" style="float: right;">
          <el-form-item label="任务名称">
            <el-input v-model="testQueryForm.taskName" placeholder="任务名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="测试结果">
            <el-select v-model="testQueryForm.testResult" placeholder="测试结果" clearable>
              <el-option label="全部" value=""></el-option>
              <el-option label="处理中" value="0"></el-option>
              <el-option label="通过" value="1"></el-option>
              <el-option label="未通过" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchTestCases">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="testCases" style="width: 100%" border stripe highlight-current-row>
        <el-table-column prop="taskName" label="任务名称"></el-table-column>
        <el-table-column prop="taskVersion" label="版本号"></el-table-column>
        <el-table-column prop="username" label="提交用户"></el-table-column>
        <el-table-column prop="testResult" label="测试结果" :formatter="formatTestResult"></el-table-column>
        <el-table-column prop="auditMessage" label="测试备注" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="创建时间"></el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="showTestDialog(scope.row)">验证</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

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

    <!-- 测试验证对话框 -->
    <el-dialog title="测试验证" :visible.sync="testDialogVisible" width="85%" @close="resetTestForm">
      <el-form ref="testForm" :model="testForm" label-width="80px">
        <el-form-item label="任务名称">
          <span>{{ currentTestTask.taskName }}</span>
        </el-form-item>
        <el-form-item label="版本号">
          <span>{{ currentTestTask.taskVersion }}</span>
        </el-form-item>
        <!-- <el-form-item label="测试SQL" prop="testCode">
          <el-input type="textarea" :rows="10" v-model="currentTestTask.testCode" readonly></el-input>
        </el-form-item> -->
        <el-form-item label="测试结果" prop="testResult">
          <el-radio-group v-model="testForm.testResult">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">未通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="测试备注" prop="auditMessage">
          <el-input type="textarea" v-model="testForm.auditMessage"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="testDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTest">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { EventBus } from '../../common/event-bus';
import CodeCompare from "../../common/CodeCompare.vue";

export default {
  components: {
    CodeCompare,
  },
  data() {
    return {
      loginUser: "",
      isAdmin: false,
      reviewTasks: [],
      testCases: [],
      currentTestTask: {},
      reviewQueryForm: {
        taskName: '',
        reviewStatus: '',
        reviewLevel: ''
      },
      testQueryForm: {
        taskName: '',
        testResult: ''
      },
      reviewDialogVisible: false,
      reviewForm: {
        auditMessage: '',
        reviewResult: '',
        id: null,
        taskId: null,
        reviewLevel: null,
        reviewStatus: null
      },
      oldCode: "",
      newCode: "",
      lastVersion: "",
      currentVersion: "",
      testDialogVisible: false,
      testForm: {
        uuid: '',
        testResult: 1,
        auditMessage: ''
      }
    };
  },
  mounted() {
    this.loginUser = sessionStorage.getItem('loginUser');
    this.checkAdminStatus().then(() => {
      if (this.isAdmin) {
        this.fetchReviewTasks();
      }
    });
    this.fetchTestCases();
  },
  methods: {
    checkAdminStatus() {
      return this.$axios.get('/user/validAdminUser', {
        params: { userName: this.loginUser }
      })
        .then(response => {
          this.isAdmin = response.data.data.items === true;
          return this.isAdmin;
        })
        .catch(error => {
          console.error('检查管理员状态失败', error);
          this.isAdmin = false;
          return false;
        });
    },
    fetchReviewTasks() {
      if (!this.isAdmin) {
        this.$message.warning('无权限访问代码审核列表');
        return;
      }
      
      const params = {
        loginUser: this.loginUser,
        taskName: this.reviewQueryForm.taskName,
        reviewStatus: this.reviewQueryForm.reviewStatus,
        reviewLevel: this.reviewQueryForm.reviewLevel
      };
      
      this.$axios.get('/review/getReviewTaskList', { params })
        .then(response => {
          this.reviewTasks = response.data.data.items;
        })
        .catch(error => {
          console.error('获取代码审核任务列表失败', error);
          this.$message.error('获取代码审核任务列表失败');
        });
    },
    fetchTestCases() {
      const params = {
        username: this.loginUser,
        ...this.testQueryForm
      };
      
      this.$axios.get('/review/getTestCases', { params })
        .then(response => {
          this.testCases = response.data.data.items;
        })
        .catch(error => {
          console.error('获取测试验证任务列表失败', error);
          this.$message.error('获取测试验证任务列表失败');
        });
    },
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
    showTestDialog(task) {
      this.currentTestTask = { ...task };
      this.testForm.uuid = task.uuid;
      this.testDialogVisible = true;
    },
    resetReviewForm() {
      this.$refs.reviewForm.resetFields();
    },
    resetTestForm() {
      this.$refs.testForm.resetFields();
      this.testForm.testResult = 1;
    },
    submitReview(result) {
      this.reviewForm.reviewResult = result;
      this.reviewForm.reviewStatus = result === 1 ? 5 : 3;

      this.$axios.get('/review/submit', {
        params: this.reviewForm
      })
        .then(response => {
          this.reviewDialogVisible = false;
          this.fetchReviewTasks();
          this.$message.success('审核提交成功');
        })
        .catch(error => {
          console.error('审核提交失败', error);
          this.$message.error('审核提交失败');
        });
    },
    submitTest() {
      this.$axios.post('/review/verificationTesting', this.testForm)
        .then(response => {
          this.testDialogVisible = false;
          this.fetchTestCases();
          this.$message.success('测试验证提交成功');
        })
        .catch(error => {
          console.error('测试验证提交失败', error);
          this.$message.error('测试验证提交失败');
        });
    },
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
    },
    formatTestResult(row, column, cellValue) {
      const statusMap = {
        0: '处理中',
        1: '通过',
        2: '未通过'
      };
      return statusMap[cellValue] || '未知状态';
    }
  }
};
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}
</style>