<!-- src/components/TaskManagerMain.vue -->
<template>
    <div class="task-manager-main">

        <div class="default-page" v-if="editableTabs.length === 0">
            当前还没有选择任何任务
        </div>

        <el-tabs v-model="editableTabsValue" type="card" closable @tab-remove="removeTab" @tab-click="clickTab(editableTabsValue)">
            <el-tab-pane v-for="(item, index) in editableTabs" :key="item.name" :label="item.title" :name="item.name">

                <el-tabs v-model="item.activeName" class="indented-tab" @tab-click="handleSencodTabClick">
                    <el-tab-pane label="数据开发" name="data-develop">

                        <div class="main-code-editor-menu">
                            <span class="main-code-editor-menu-left">
                                <span
                                    style="float: right; color: #8492a6; font-size: 13.5px; margin-right: 5px;">Versions:</span>
                                <el-select v-model="item.defaultVersion" placeholder="请选择" size="mini"
                                    style="width: 400px;" @change="handleVersionChange">
                                    <el-option v-for="ele in item.taskVersions" :key="ele.version" :label="ele.version"
                                        :value="ele.version">
                                        <span style="float: left;font-size: 12px">
                                            {{ new Date(ele.createTime).toISOString().replace(/T/, ' ').substr(0, 19) }}
                                        </span>
                                        <div style="float: right; color: #8492a6; font-size: 12px">
                                            <span style="margin-right: 10px;">
                                                {{ ele.isOnline === 1 ? '当前版本' : ele.isReleased === 1 ? '已发版' : '未发版' }}
                                            </span>
                                            <span>{{ ele.version }}</span>
                                        </div>
                                    </el-option>
                                </el-select>
                            </span>

                            <span class="main-code-editor-menu-right">


                                <el-dropdown size="mini" style="margin-right: 10px;">
                                    <el-button size="mini" type="info" plain>
                                        参数说明<i class="el-icon-arrow-down el-icon--right"></i>
                                    </el-button>
                                    <el-dropdown-menu slot="dropdown">
                                        <el-dropdown-item v-for="(item, index) in paramList" :key="index">

                                            <div style="display: flex;justify-content: space-between; width: 390px;">
                                                <span
                                                    style="display: flex; align-items: center;justify-content: left;">{{
                                                        item.paramName }}</span>
                                                <span
                                                    style="display: flex; align-items: center;justify-content: right;">{{
                                                        item.paramDesc }}</span>
                                            </div>

                                        </el-dropdown-item>
                                    </el-dropdown-menu>
                                </el-dropdown>


                                <el-button type="primary" size="mini" plain @click="handleCodeAreaEdit">{{ item.editName
                                    }}</el-button>
                                <el-button type="primary" size="mini" plain @click="handleCodeAreaSave">保存</el-button>

                                <el-dropdown @command="handleExecute" style="margin-left: 10px; margin-right: 10px;">
                                    <el-button type="primary" size="mini" plain>
                                        执行
                                        <i class="el-icon-arrow-down el-icon--right"></i>
                                    </el-button>
                                    <el-dropdown-menu slot="dropdown">
                                        <el-dropdown-item command="test">测试</el-dropdown-item>
                                        <el-dropdown-item :disabled="isOnlineTask" command="production">生产</el-dropdown-item>
                                    </el-dropdown-menu>
                                </el-dropdown>

                                <div>
                                    <el-dialog :visible.sync="dialogVisible" title="数据回刷">
                                        <el-form :model="form">
                                            <el-form-item label="回刷周期">
                                                <el-select v-model="form.period" placeholder="请选择回刷周期">
                                                    <el-option label="按日" value="day"></el-option>
                                                    <el-option label="按月" value="month"></el-option>
                                                </el-select>
                                            </el-form-item>
                                            <el-form-item label="开始日期">
                                                <el-date-picker v-model="form.startDate" type="date"
                                                    placeholder="选择开始日期"></el-date-picker>
                                            </el-form-item>
                                            <el-form-item label="结束日期">
                                                <el-date-picker v-model="form.endDate" type="date"
                                                    placeholder="选择结束日期"></el-date-picker>
                                            </el-form-item>
                                        </el-form>
                                        <div slot="footer" class="dialog-footer">
                                            <el-button @click="dialogVisible = false">取消</el-button>
                                            <el-button type="primary" @click="submitRefresh">确定</el-button>
                                        </div>
                                    </el-dialog>
                                </div>

                                <div>
                                    <!-- 发布按钮 -->
                                    <el-button type="primary" size="mini" plain
                                        @click="showPublishDialog">发布</el-button>

                                    <!-- 弹出框 -->
                                    <el-dialog title="代码上线变更理由" :visible.sync="publishDialogVisible" width="30%">
                                        <el-form :model="publishForm">
                                            <el-form-item label="变更理由">
                                                <el-input type="textarea" v-model="publishForm.reason"
                                                    rows="4"></el-input>
                                            </el-form-item>
                                        </el-form>
                                        <div slot="footer" class="dialog-footer">
                                            <el-button @click="publishDialogVisible = false">取消</el-button>
                                            <el-button type="primary" @click="handlePublish">确认</el-button>
                                        </div>
                                    </el-dialog>
                                </div>
                            </span>
                        </div>


                        <div class="main-code-editor-area" style="margin-top: 10px;">
                            <monaco-editor v-model="item.dataDevelop.codeArea" language="sql" theme="vs"
                                :options="editorOptions" :readOnly="item.dataDevelop.isCodeAreaReadOnly" />
                        </div>

                        <!-- <div v-show="item.dataDevelop.isVisibleLogging" style="margin-top: 10px;">
                            <span>
                                执行日志
                            </span>
                            <el-input type="textarea" size="small" :autosize="{ minRows: 20 }"
                                v-model="item.dataDevelop.logArea" readonly>
                            </el-input>
                        </div> -->

                    </el-tab-pane>





                    <!-- 调度模块 -->
                    <el-tab-pane label="调度配置" name="schedule-config">
                        <div class="schedule-config">
                            <el-form :label-position="item.scheduleConfig.labelPosition" label-width="100px" :model="item.scheduleConfig" size="mini" class="form-layout">
                                <el-row>
                                    <el-col :span="22">
                                        <h3>编辑区</h3>
                                    </el-col>
                                    <el-col :span="2">
                                        <el-button @click="reEdit">重置</el-button>
                                    </el-col>
                                </el-row>



                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="调度项目">
                                            <el-input v-model="item.scheduleConfig.project" disabled></el-input>
                                        </el-form-item>
                                    </el-col>
                                    <el-col :span="12">
                                        <el-form-item label="任务类型">
                                            <el-input v-model="item.scheduleConfig.taskType" disabled></el-input>
                                        </el-form-item>
                                    </el-col>
                                </el-row>



                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="调度名称">
                                            <div style="display: flex; align-items:flex-start">
                                                <span>
                                                    {{ item.scheduleConfig.taskNodeName }}
                                                </span>
                                                <el-input v-model="item.scheduleConfig.taskNodeNameSuffix"
                                                    :disabled="!isEditable" style="flex-grow: 1; margin-left: 2px;">
                                                </el-input>
                                            </div>
                                        </el-form-item>
                                    </el-col>

                                    <el-col :span="12">
                                        <el-form-item label="优先级">
                                            <el-select v-model="item.scheduleConfig.priority" placeholder="请选择优先级"
                                                style="width: 100%">
                                                <el-option label="低" value="lower"></el-option>
                                                <el-option label="中" value="medium"></el-option>
                                                <el-option label="高" value="higher"></el-option>
                                            </el-select>
                                        </el-form-item>
                                    </el-col>
                                </el-row>



                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="重试间隔">
                                            <el-input-number v-model="item.scheduleConfig.retryInterval" :min="1"
                                                :max="30" :step="1" :value="1" label="重试间隔" style="width: 100%"
                                                controls-position="right" class="align-left">
                                            </el-input-number>
                                        </el-form-item>
                                    </el-col>
                                    <el-col :span="12">
                                        <el-form-item label="重试次数">
                                            <el-input-number v-model="item.scheduleConfig.retryTimes" :min="0" :max="5"
                                                :step="1" :value="3" label="重试次数" style="width: 100%"
                                                controls-position="right" class="align-left">
                                            </el-input-number>
                                        </el-form-item>
                                    </el-col>
                                </el-row>



                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="调度周期">
                                            <el-select v-model="item.scheduleConfig.period" placeholder="请选择调度周期"
                                                style="width: 100%">
                                                <el-option label="天" value="day" default></el-option>
                                                <!-- <el-option label="周" value="week"></el-option>
                                                <el-option label="月" value="month"></el-option>
                                                <el-option label="时" value="hour"></el-option> -->
                                            </el-select>
                                        </el-form-item>
                                    </el-col>
                                    <el-col :span="12">
                                        <el-form-item label="开始时间">
                                            <el-input v-model="item.scheduleConfig.beginTime" style="width: 100%"
                                                disabled></el-input>
                                        </el-form-item>
                                    </el-col>

                                </el-row>





                                <el-form-item label="依赖任务">
                                    <el-divider></el-divider>
                                    <div class="align-end">
                                        <el-button type="primary" size="mini" plain @click="fetchTaskDependencies">依赖识别</el-button>
                                    </div>

                                    <el-empty v-if="!item.scheduleConfig.dependency.length" description="暂无依赖数据" style="height: 5px;"></el-empty>

                                    <el-table v-else :data="item.scheduleConfig.dependency" style="width: 100%">
                                        <el-table-column label="上游依赖">
                                            <template slot-scope="scope">
                                                <el-popover trigger="hover" placement="top">
                                                    <div slot="reference" class="name-wrapper">
                                                        <el-tag size="medium">{{ scope.row.dependencyName }}</el-tag>
                                                    </div>
                                                </el-popover>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="依赖识别类型">
                                            <template slot-scope="scope">
                                                <el-tag type="info" size="medium">{{ scope.row.joinTypeDesc }}</el-tag>
                                            </template>
                                        </el-table-column>

                                        <el-table-column label="操作">
                                            <template slot-scope="scope">
                                                <div class="operation-buttons">
                                                    <el-button size="mini" type="danger"
                                                        @click="handleDeleteDependency(scope.$index, scope.row)">删除</el-button>
                                                </div>
                                            </template>
                                        </el-table-column>
                                    </el-table>

                                    <div class="align-end margin-top">
                                        <el-button type="primary" size="mini" plain @click="showAddDialog">添加</el-button>
                                    </div>



                                    <el-dialog title="添加/编辑依赖" :visible.sync="updateDialogVisible" width="30%">
                                        <el-form :model="currentDependency" ref="dependencyForm" label-width="120px">
                                            <el-form-item label="依赖名称" prop="dependency"
                                                :rules="[{ required: true, message: '请输入依赖名称', trigger: 'blur' }]">
                                                <el-input v-model="currentDependency.dependencyName"></el-input>
                                            </el-form-item>
                                            <el-form-item label="依赖识别类型" prop="joinTypeDesc"
                                                :rules="[{ required: true, message: '请选择依赖识别类型', trigger: 'change' }]">
                                                <el-select v-model="currentDependency.joinTypeDesc" placeholder="请选择类型"
                                                    disabled>
                                                    <el-option label="人工维护" value="人工维护" selected></el-option>
                                                    <!-- 更多选项已移除，因为类型是固定的 -->
                                                </el-select>
                                            </el-form-item>
                                        </el-form>
                                        <span slot="footer" class="dialog-footer">
                                            <el-button @click="updateDialogVisible = false">取 消</el-button>
                                            <el-button type="primary" @click="saveDependency">{{ isEditMode ? '保存' :
                                                '添加' }}</el-button>
                                        </span>
                                    </el-dialog>



                                    <!-- 删除确认对话框 -->
                                    <el-dialog title="确认删除" :visible.sync="deleteDialogVisible" width="30%">
                                        <p>确定删除依赖: "{{ currentDependency.dependencyName }}" 吗？</p>
                                        <span slot="footer" class="dialog-footer">
                                            <el-button @click="deleteDialogVisible = false">取 消</el-button>
                                            <el-button type="primary" @click="confirmDelete">确 定</el-button>
                                        </span>
                                    </el-dialog>
                                </el-form-item>



                                <div class="align-end margin-top">
                                    <el-button type="primary" size="mini" plain @click="confirmSaveTaskDependency">保存</el-button>
                                </div>


                                <div style="margin-top: 35px;">
                                    <el-divider content-position="left">
                                        <h3>在行调度列表</h3>
                                    </el-divider>

                                    <el-descriptions v-for="(schedule, index) in item.scheduleList" :key="index" :title="'调度名称: ' + schedule.scheduleName + ' ;  状态: ' + (schedule.isOnline ? '在线运行' : '下线状态')" :border="true">
                                        <el-descriptions-item label="调度项目">
                                            {{ schedule.project }}
                                        </el-descriptions-item>
                                        <el-descriptions-item label="任务类型">
                                            {{ schedule.taskType === 1 ? 'shell' : '未知类型'}}
                                        </el-descriptions-item>
                                        <el-descriptions-item label="优先级">
                                            {{ schedule.taskPriority === 1 ? 'lower' : schedule.taskPriority === 2?'medium' : schedule.taskPriority === 3? 'higher' : 'other'}}
                                        </el-descriptions-item>
                                        <el-descriptions-item label="重试间隔(分)">
                                            {{schedule.failRetryInterval}}
                                        </el-descriptions-item>
                                        <el-descriptions-item label="重试次数">
                                            {{ schedule.failRetryTimes}}
                                        </el-descriptions-item>
                                        <el-descriptions-item label="调度周期">
                                            {{ schedule.period }}
                                        </el-descriptions-item>
                                        <el-descriptions-item label="开始时间">
                                            {{schedule.beginTime}}
                                        </el-descriptions-item>
                                        <!-- <el-descriptions-item label="调度状态">
                                            {{schedule.isOnline ? '下线状态' : '运行状态'}}
                                        </el-descriptions-item> -->

                                        <el-descriptions-item label="操作">
                                            <div class="align-end margin-top" style="text-align: right;">
                                                <el-button type="primary" size="mini" plain @click="handleOnline(schedule)"> 
                                                    {{ schedule.isOnline ? '下线' : '上线' }}
                                                </el-button>
                                                <el-button type="primary" size="mini" plain :disabled="schedule.isOnline" @click="handleEditConfig(schedule)">编辑</el-button>
                                                <el-button type="danger" size="mini" plain :disabled="schedule.isOnline" @click="handleDeleteConfig(schedule)">删除</el-button>
                                            </div>
                                        </el-descriptions-item>
                                    </el-descriptions>
                                </div>

                            </el-form>
                        </div>
                    </el-tab-pane>


                    <el-tab-pane label="任务管理" name="subtask-manager" disabled>
                    </el-tab-pane>


                </el-tabs>

            </el-tab-pane>

        </el-tabs>
    </div>
</template>

<script>
import CodeEditor from '../../common/CodeEditor.vue';
import { EventBus } from '../../common/event-bus';
import moment from 'moment-timezone';
import { MessageBox, Message } from 'element-ui';
import MonacoEditor from '../../common/MonacoEditor.vue';

export default {
    name: 'TaskManagerMain',
    components: {
        CodeEditor,
        MonacoEditor
    },
    data() {
        return {
            loginUser: "",
            selectedTasks: [],
            activeTask: "",
            activeTabs: [],
            editableTabsValue: '1',
            editableTabs: [],
            tabIndex: 1,
            currentSelectedTab: {},
            paramList: [],
            environment: '',
            dialogVisible: false,
            form: {
                period: '',
                startDate: '',
                endDate: ''
            },
            publishDialogVisible: false,
            publishForm: {
                reason: ''
            },

            // dependency 
            updateDialogVisible: false,
            currentDependency: {
                dependencyName: '',
                joinTypeDesc: '人工维护'
            },
            isEditMode: false,
            dependencyToDelete: null,
            isEditable: true,
            deleteDialogVisible: false,
            pickerOptions: {
                // 你可以根据需求添加更多选项
                selectableRange: '00:00:00 - 23:59:59', // 可选时间范围
            },
            editorOptions: {
                automaticLayout: true,      // 关键！启用自动布局
                scrollBeyondLastLine: false,
                minimap: { enabled: false },// 关闭缩略图节省空间
                fontSize: 13,
                lineHeight: 20,
                wordWrap: 'on',            // 自动换行
                wrappingIndent: 'indent',  // 换行缩进
                fixedOverflowWidgets: true,// 固定悬浮组件位置
                renderLineHighlight: 'all', // 高亮当前行
                lineNumbersMinChars: 3,    // 行号最小宽度
                scrollbar: {
                    verticalScrollbarSize: 8,
                    horizontalScrollbarSize: 8,
                    useShadows: false
                }
            }
        }
    },
    created() {
        EventBus.$on('login-user', data => {
            this.loginUser = sessionStorage.getItem('loginUser');
        });
        EventBus.$on('tasks-activeTask', data => {
            this.activeTask = data;
        });
        EventBus.$on('tasks-selectedTasks', data => {
            this.selectedTasks = data;
        });
    },
    mounted() {
        this.loginUser = sessionStorage.getItem('loginUser');
        this.loadParams();
        // 设置定时器，每6小时（即6 * 60 * 60 * 1000毫秒）执行一次方法  
        setInterval(this.loadParams, 6 * 60 * 60 * 1000);
    },
    watch: {
        async activeTask(newActiveTask) {
            this.activeTask = newActiveTask;
            const index = this.selectedTasks.indexOf(this.activeTask);
            const flag = this.activeTabs.indexOf(index + '');
            if (flag === -1) {
                const currentTaskInfo = await this.getCurrentTaskInfo(); // 等待获取数据
                this.addTab(this.activeTask, index + '', currentTaskInfo)
                this.activeTabs.push(index + '')
            }
            const currentTab = this.editableTabs.find(tab => tab.title === this.activeTask);
            this.editableTabsValue = currentTab.name;
        },
        selectedTasks(newSelectedTasks) {
            this.selectedTasks = newSelectedTasks;
        }
    },
    methods: {
        async getCurrentTaskInfo() {
            try {
                const response = await this.$axios.get("/develop/getCurrentTaskInfo", {
                    params: { taskName: this.activeTask }
                });
                if (response.data && response.data.status === 'success') {
                    const taskVersions = await this.getTaskVersions(this.activeTask, -1)
                    return [response.data.data.items, taskVersions];
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        addTab(titleName, targetName, currentTaskInfo) {
            const taskObject = currentTaskInfo[0];
            const taskVersionsInfo = currentTaskInfo[1];
            this.editableTabs.push({
                taskInfo: taskObject,
                title: titleName,
                name: targetName,
                activeName: "data-develop",
                editName: '只读',
                taskVersionsInfo: taskVersionsInfo,
                defaultVersion: taskVersionsInfo.currentVersion,
                taskVersions: taskVersionsInfo.caesarTaskVos,
                dataDevelop: {
                    language: 'sql',
                    codeArea: taskObject.taskScript,
                    logArea: "",
                    isCodeAreaReadOnly: true,
                    isVisibleLogging: true
                },
                scheduleConfig: {
                    labelPosition: 'right',
                    system: "DolphinScheduler",
                    project: "数仓调度项目",
                    taskNodeName: '',
                    taskType: "shell",
                    priority: "lower",
                    retryTimes: "3",
                    retryInterval: "5",
                    beginTime: "00:15:00",
                    taskNodeNameSuffix: "",
                    period: "day",
                    dependency: [
                        // {
                        //     dependencyName: 'ods.dim_date',
                        //     joinTypeDesc: "自动识别"
                        // }
                    ]
                },
                scheduleList: []
            });
            this.editableTabsValue = targetName;
        },
        removeTab(targetName) {
            let tabs = this.editableTabs;
            let activeName = this.editableTabsValue;
            if (activeName === targetName) {
                tabs.forEach((tab, index) => {
                    if (tab.name === targetName) {
                        let nextTab = tabs[index + 1] || tabs[index - 1];
                        if (nextTab) {
                            activeName = nextTab.name;
                        }
                    }
                });
            }
            const index = this.activeTabs.indexOf(targetName);
            if (index !== -1) {
                var item = this.activeTabs.splice(index, 1);
            }
            this.editableTabsValue = activeName;
            this.editableTabs = tabs.filter(tab => tab.name !== targetName);
        },
        handleCodeAreaEdit() {
            // 找到当前激活的标签页对应的项
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            if (currentTab) {
                // 取反 isCodeAreaReadOnly 属性
                currentTab.dataDevelop.isCodeAreaReadOnly = !currentTab.dataDevelop.isCodeAreaReadOnly;
                if (currentTab.dataDevelop.isCodeAreaReadOnly) {
                    currentTab.editName = '只读';
                } else {
                    currentTab.editName = '编辑';
                }
            }
        },
        async handleCodeAreaSave() { // 计划增加-版本控制逻辑
            // 找到当前激活的标签页对应的项
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            const taskInfo = currentTab.taskInfo;
            const code = currentTab.dataDevelop.codeArea;
            this.activeTask = taskInfo.taskName;
            // 调用后端插入数据 
            try {
                console.log('save task == ' + JSON.stringify(taskInfo))
                const updateUser = await this.getUserIdFromUsername(this.loginUser);
                const response = await this.$axios.post("/develop/saveTask", {
                    menuId: taskInfo.menuId,
                    taskType: taskInfo.taskType,
                    taskName: taskInfo.taskName,
                    datasourceInfo: taskInfo.datasourceInfo,
                    engine: taskInfo.engine,
                    groupId: taskInfo.groupId,
                    taskScript: code,
                    lastVersion: taskInfo.version,
                    createdUser: taskInfo.createdUser,
                    updatedUser: updateUser,
                    createTime: taskInfo.createTime
                });
                if (response.data && response.data.status === 'success') {
                    const taskVersions = await this.getTaskVersions(this.activeTask, response.data.data.items);
                    const getTaskInfo = await this.getCurrentTaskInfoWithVersion(this.activeTask, taskVersions.currentVersion)
                    currentTab.taskInfo = getTaskInfo;
                    currentTab.taskVersionsInfo = taskVersions;
                    currentTab.taskVersions = taskVersions.caesarTaskVos;
                    currentTab.defaultVersion = taskVersions.currentVersion;
                }
                if (response.data.status === 'fail') {
                    alert(response.data.message);
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        async getUserIdFromUsername(userName) {
            const response = await this.$axios.get("/user/getUserIdFromUsername", {
                params: {
                    userName: userName
                }
            });
            if (response.data && response.data.status === 'success') {
                return response.data.data.items;
            }
        },
        async getCurrentTaskInfoWithVersion(taskName, version) {
            try {
                const response = await this.$axios.get("/develop/getCurrentTaskInfoWithVersion", {
                    params: {
                        taskName: taskName,
                        version: version
                    }
                });
                if (response.data && response.data.status === 'success') {
                    return response.data.data.items;
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        async getTaskVersions(taskName, currentVersion) {
            try {
                const response = await this.$axios.get("/develop/getTaskVersions", {
                    params: {
                        taskName: taskName,
                        currentVersion: currentVersion
                    }
                });
                if (response.data && response.data.status === 'success') {
                    return response.data.data.items;
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        clickTab(thisTab, event) {
            const currentTab = this.editableTabs.find(tab => tab.name === thisTab);
            this.currentSelectedTab = currentTab;
        },
        handleSencodTabClick(thisTab, event) {
            if (thisTab.name === "schedule-config") {
                this.fetchScheduleList()
                this.fetchScheduleBaseInfo()
            }
        },
        handleVersionChange(selectedVersion) {
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            const selectedVersionData = currentTab.taskVersions.find(ele => ele.version === selectedVersion);
            currentTab.dataDevelop.codeArea = selectedVersionData.taskScript;
            currentTab.defaultVersion = selectedVersionData.version;
            currentTab.taskInfo = selectedVersionData;
        },
        async loadParams() {
            try {
                const response = await this.$axios.get("/develop/getParams");
                if (response.data && response.data.status === 'success') {
                    this.paramList = response.data.data.items;
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        changeTextarea(code) {
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            currentTab.dataDevelop.codeArea = code
        },
        async handleExecute(environment) {
            this.dialogVisible = true;
            this.environment = environment;
        },
        validateForm() {
            if (!this.form.period || !this.form.startDate || !this.form.endDate) {
                this.$message.error('请完整填写表单');
                return false;
            }

            const start = new Date(this.form.startDate);
            const end = new Date(this.form.endDate);

            if (end < start) {
                this.$message.error('结束日期不能小于开始日期');
                return false;
            }

            if (this.form.period === 'month') {
                if (start.getDate() !== end.getDate()) {
                    this.$message.error('对于月粒度，开始日期和结束日期的日必须相同');
                    return false;
                }
            }

            return true;
        },
        async submitRefresh() {
            if (!this.validateForm()) {
                return;
            }

            try {
                const format = 'YYYY-MM-DD';
                const timezone = 'Asia/Shanghai';
                const startDate = moment(this.form.startDate).tz(timezone).format(format);
                const endDate = moment(this.form.endDate).tz(timezone).format(format);

                const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
                const requestData = {
                    version: currentTab.taskInfo.version,
                    taskName: currentTab.taskInfo.taskName,
                    environment: this.environment,
                    period: this.form.period,
                    startDate: startDate,
                    endDate: endDate,
                    username: this.loginUser
                }
                const response = await this.$axios.post('/develop/refresh', requestData);
                if (response.data.status === 'success') {
                    this.$message.success('回刷请求已发送');
                } else {
                    this.$message.error('回刷请求失败');
                }
            } catch (error) {
                this.$message.error('回刷请求失败');
            } finally {
                this.dialogVisible = false;
                this.form.period = '';
                this.form.startDate = '';
                this.form.endDate = '';
            }
        },
        showPublishDialog() {
            this.publishDialogVisible = true;
        },
        async handlePublish() {
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            const response = await this.$axios.post('/review/publish', {

                version: currentTab.taskInfo.version,
                taskName: currentTab.taskInfo.taskName,
                submitUsername: this.loginUser,
                codeDesc: this.publishForm.reason

            });

            // 发送代码上线请求的逻辑，例如调用API
            this.$message.success('代码上线请求已发送');
            this.publishDialogVisible = false;
        },


        // ---------------------------------------------------------------------------------------------------------------
        // 调度模块方法定义 -------------------------------------------------------------------------------------------------
        // ---------------------------------------------------------------------------------------------------------------
        // 调度模块方法定义
        async fetchScheduleList() {
            try {
                const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
                const response = await this.$axios.get('/scheduler/getTaskSchedules', {
                    params: {
                        taskName: currentTab.taskInfo.taskName
                    }
                });
                // 根据后端接口修改URL
                currentTab.scheduleList = response.data.data.items;
                console.log("当前任务"+currentTab.taskInfo.taskName+"获取到的调度列表信息如下: " + JSON.stringify(currentTab.scheduleList))
            } catch (error) {
                console.error('Error fetching schedules:', error);
            }
        },
        async fetchScheduleBaseInfo() {
            try {
                const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
                const response = await this.$axios.get('/scheduler/getScheduleBaseInfo');
                // 根据后端接口修改URL
                const baseInfo = response.data.data.items;
                if (baseInfo.scheduleCategory === 1) {
                    currentTab.scheduleConfig.system = "DolphinScheduler";
                } else {
                    currentTab.scheduleConfig.system = "Hera";
                }
                currentTab.scheduleConfig.projects = baseInfo.projects;
                currentTab.scheduleConfig.project = baseInfo.projects.day;
                currentTab.scheduleConfig.labelPosition = 'right';
                currentTab.scheduleConfig.taskNodeName = currentTab.taskInfo.taskName + ".";
                currentTab.scheduleConfig.taskNodeNameSuffix = "";
                currentTab.scheduleConfig.taskType = "shell";
                currentTab.scheduleConfig.priority = "lower";
                currentTab.scheduleConfig.retryTimes = "3";
                currentTab.scheduleConfig.retryInterval = "5";
                currentTab.scheduleConfig.beginTime = "00:15:00";
                currentTab.scheduleConfig.period = "day";
            } catch (error) {
                console.error('Error fetching schedule base info:', error);
            }
        },
        // 点击依赖识别按钮时触发，向后端发送请求并获取依赖数据
        async fetchTaskDependencies() {
            try {
                const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
                const response = await this.$axios.get('/scheduler/getTaskDependencies', {
                    params: {
                        taskName: currentTab.taskInfo.taskName,
                        taskVersion: currentTab.taskInfo.version,
                        period: currentTab.scheduleConfig.period
                    }
                });
                // 根据后端接口修改URL
                currentTab.scheduleConfig.dependency = response.data.data.items;
            } catch (error) {
                console.error('Error fetching schedules:', error);
            }
        },


        // 添加依赖按钮 
        showAddDialog() {
            this.isEditMode = false;
            this.currentDependency = { dependencyName: '', joinTypeDesc: '人工维护' };
            this.updateDialogVisible = true;
        },
        handleEditDependency(index, row) {
            this.isEditMode = true;
            this.currentDependency = { ...row };
            this.updateDialogVisible = true;
        },
        handleDeleteDependency(index, row) {
            this.isEditMode = false;
            this.dependencyToDelete = row;
            this.currentDependency = { ...row };
            this.deleteDialogVisible = true;
        },


        saveDependency() {
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            currentTab.scheduleConfig.dependency.push({ ...this.currentDependency });
            this.updateDialogVisible = false;
            this.currentDependency = { dependencyName: '', joinTypeDesc: '人工维护' };
        },

        confirmDelete() {
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            const index = currentTab.scheduleConfig.dependency.indexOf(this.dependencyToDelete);
            if (index > -1) {
                currentTab.scheduleConfig.dependency.splice(index, 1);
            }
            this.deleteDialogVisible = false;
            this.dependencyToDelete = null;
            this.currentDependency = { dependencyName: '', joinTypeDesc: '人工维护' };
        },

        confirmSaveTaskDependency() {
            // 弹出二次确认框
            MessageBox.confirm('确认要保存依赖吗？', '二次确认', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                console.log('用户确认保存');  // 调试用，确认是否进入这里
                this.handleSaveTaskDependency();
            }).catch(() => {
                // 用户取消时的提示
                Message.info('已取消保存');
            });
        },
        convertToDependency(dependencyList) {
            return dependencyList.map(dep => {
                var joinType = 1;
                if (dep.joinTypeDesc === "自动识别") {
                    joinType = 1;
                } else {
                    joinType = 2;
                }
                return {
                    preScheduleCode: null,
                    preScheduleName: dep.dependencyName,
                    joinType: joinType, // 默认 joinType 为 1
                    ownerName: this.loginUser
                };
            });
        },
        async handleSaveTaskDependency() {
            this.isEditable = true;
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            var taskPriority = 2;
            if (currentTab.scheduleConfig.priority === "lower") {
                taskPriority = 1;
            } else if (currentTab.scheduleConfig.priority === "medium") {
                taskPriority = 2;
            } else if (currentTab.scheduleConfig.priority === "higher") {
                taskPriority = 3;
            }
            var tmpScheduleName = currentTab.taskInfo.taskName
            if(currentTab.scheduleConfig.taskNodeNameSuffix != "" && currentTab.scheduleConfig.taskNodeNameSuffix != null){
                tmpScheduleName = tmpScheduleName + "." + currentTab.scheduleConfig.taskNodeNameSuffix 
            }else {
                tmpScheduleName = currentTab.scheduleConfig.taskNodeName
            }

            // 准备要发送的数据
            const data = {
                taskName: currentTab.taskInfo.taskName,
                taskVersion: currentTab.taskInfo.version,
                scheduleCategory: 1,
                project: currentTab.scheduleConfig.project,
                scheduleCode: null,
                scheduleName: tmpScheduleName,
                releaseStatus: 2,
                taskType: 1,
                scheduleParams: "[]",
                taskPriority: taskPriority,
                failRetryTimes: currentTab.scheduleConfig.retryTimes,
                failRetryInterval: currentTab.scheduleConfig.retryInterval,
                beginTime: currentTab.scheduleConfig.beginTime,
                period: currentTab.scheduleConfig.period,
                dateValue: currentTab.scheduleConfig.dateValue,
                version: null,
                taskCode: null,
                ownerName: this.loginUser,
                dependency: this.convertToDependency(currentTab.scheduleConfig.dependency)
            }

            console.log("本次保存任务信息 " + JSON.stringify(data) + " 后缀 " + currentTab.scheduleConfig.taskNodeNameSuffix)
            this.sendCreateOrUpdate(data, currentTab.scheduleConfig.taskNodeNameSuffix)
        },
        async sendCreateOrUpdate(data, shuffix) {
            if (this.isEditMode) {
                if (shuffix !== "" || null !== shuffix) {
                    // 发送 POST 请求到后端
                    await this.$axios.post('/scheduler/genTaskSchedule', data)
                        .then(response => {
                            console.log("打印更新结果 " + JSON.stringify(response))
                            // 请求成功后的处理
                            if (response.data.status=="success") {
                                Message.success('保存成功');
                            } else {
                                Message.error('保存失败：' + response.data.message);
                            }
                            this.reEdit()
                            this.fetchScheduleList()
                        })
                        .catch(error => {
                            // 请求失败的处理
                            Message.error('请求失败：' + error.message);
                        });
                } else {
                    alert("调度名称尾缀不能为空，请输入正确业务含义标识!")
                }
            } else {
                // 发送 POST 请求到后端
                // await this.$axios.post('/scheduler/updateTaskSchedule', data)
                await this.$axios.post('/scheduler/genTaskSchedule', data)
                    .then(response => {
                        console.log("打印更新结果 " + JSON.stringify(response))
                        // 请求成功后的处理
                        if (response.data.status=="success") {
                            Message.success('保存成功');
                        } else {
                            Message.error('保存失败：' + response.data.message);
                        }
                        this.reEdit()
                        this.fetchScheduleList()
                    })
                    .catch(error => {
                        // 请求失败的处理
                        Message.error('请求失败：' + error.message);
                    });
            }
        },
        async handleOnline(schedule) { // 切换调度上线/下线 
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            schedule.isOnline = !schedule.isOnline
            try {
                var releaseState = 0
                var releaseStateDesc = "下线"
                if(schedule.isOnline){
                    releaseState = 1
                    releaseStateDesc = "上线"
                }

                await this.$confirm(
                    `确定要${releaseStateDesc}任务 "${schedule.scheduleName}" 吗？此操作不可撤销。`,
                    '确认发布',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                );

                const response = await this.$axios.get("/scheduler/releaseSchedule", {
                    params: { 
                        scheduleName: schedule.scheduleName,
                        releaseState: releaseState 
                    }
                });

                if (response.data && response.data.status === 'success') {
                    this.$message({
                        type: 'success',
                        message: '执行成功',
                    });
                    // TODO: 更新数据列表，比如重新加载表格
                } else {
                    this.$message.error('执行失败，请检查后再试');
                }
                this.fetchScheduleList()
            } catch (error) {
                if (error !== 'cancel') {
                    console.error("执行出错:", error);
                    this.$message.error('执行过程中发生错误');
                }
            }
        },
        async handleEditConfig(schedule) {

            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            currentTab.scheduleConfig.labelPosition = 'right',
            currentTab.scheduleConfig.system = "DolphinScheduler",
            currentTab.scheduleConfig.project = schedule.project,
            currentTab.scheduleConfig.taskNodeName = schedule.scheduleName,
            currentTab.scheduleConfig.taskType = (schedule.taskType === 1 ? 'shell' : 'other'),
            currentTab.scheduleConfig.priority = (schedule.taskPriority === 1 ? 'lower' : schedule.taskPriority === 2 ? 'medium' : schedule.taskPriority === 3 ? 'higher' : 'other'),
            currentTab.scheduleConfig.retryTimes = schedule.failRetryTimes,
            currentTab.scheduleConfig.retryInterval = schedule.failRetryInterval,
            currentTab.scheduleConfig.beginTime = schedule.beginTime,
            currentTab.scheduleConfig.taskNodeNameSuffix = "",
            currentTab.scheduleConfig.period = schedule.period
            currentTab.scheduleConfig.dependency = schedule.dependency.map(dep => {
                var joinTypeDesc = "自动识别";
                if (dep.joinType == 2) {
                    joinTypeDesc = "人工维护"
                }
                return {
                    dependencyName: dep.preScheduleName,
                    joinTypeDesc: joinTypeDesc
                };
            });
            this.isEditable = false;

            console.log("编辑区获取到依赖信息" + JSON.stringify(schedule))
        },
        async handleDeleteConfig(schedule) {
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            try {
                await this.$confirm(
                    `确定要删除任务 "${schedule.scheduleName}" 吗？此操作不可撤销。`,
                    '确认删除',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                );

                console.log("开始删除任务 " + schedule.scheduleName);
                const response = await this.$axios.get("/scheduler/deleteTaskSchedule", {
                    params: { scheduleName: schedule.scheduleName }
                });

                if (response.data && response.data.status === 'success') {
                    this.$message({
                        type: 'success',
                        message: '删除成功',
                    });
                    // TODO: 更新数据列表，比如重新加载表格
                } else {
                    this.$message.error('删除失败，请稍后再试');
                }
                this.fetchScheduleList()
            } catch (error) {
                if (error !== 'cancel') {
                    console.error("删除出错:", error);
                    this.$message.error('删除过程中发生错误');
                }
                // 如果是用户点击取消，不需要做任何处理
            }
        },
        reEdit() {
            this.fetchScheduleBaseInfo();
            this.isEditable = true;
        }
        // ---------------------------------------------------------------------------------------------------------------
        // 调度模块方法定义结束 ---------------------------------------------------------------------------------------------
        // ---------------------------------------------------------------------------------------------------------------
    },
    computed: {
        isOnlineTask() {
            const currentTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue);
            return !(currentTab.taskInfo.isOnline === 1);
        }
    }
}
</script>


<style scoped>
.task-manager-main {
    width: 100%;
}

.main-code-editor-menu {
    display: flex;
    justify-content: space-between;
    margin-right: 20px;
    font-size: 16px;
}

.main-code-editor-menu-left {
    display: flex;
    align-items: center;
    justify-content: left;
    margin-inline: 20px;
}

.main-code-editor-menu-right {
    display: flex;
    align-items: center;
    justify-content: right;
}

.main-code-editor-area {
    margin-top: 10px;
}

.indented-tab {
    margin-left: 30px;
}

.versionTitle {
    font-family: "PingFang SC";
    font-size: 16px;
}

.default-page {
    font-size: 1.5em;
    color: #333;
    display: flex;
    justify-content: center;
    align-items: center;
    height: calc(100vh - 120px);
    text-align: center;
}
</style>
