<!-- src/components/TaskManagerMain.vue -->
<template>
    <div class="task-manager-main">

        <div class="default-page" v-if="editableTabs.length === 0">
            当前还没有选择任何任务
        </div>

        <el-tabs v-model="editableTabsValue" type="card" closable @tab-remove="removeTab" @tab-click="clickTab(editableTabsValue)">

            <el-tab-pane v-for="(item, index) in editableTabs" :key="item.name" :label="item.title" :name="item.name">

                <el-tabs v-model="item.activeName" class="indented-tab">
                    <el-tab-pane label="数据开发" name="data-develop">

                        <div class="main-code-editor-menu">
                            <span class="main-code-editor-menu-left">
                                <span style="float: right; color: #8492a6; font-size: 13.5px; margin-right: 5px;">Versions:</span>
                                <el-select v-model="item.defaultVersion" placeholder="请选择" size="mini" style="width: 400px;" @change="handleVersionChange">
                                    <el-option v-for="ele in item.taskVersions" :key="ele.version" :label="ele.version" :value="ele.version">
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
                                                <span style="display: flex; align-items: center;justify-content: left;">{{ item.paramName }}</span>
                                                <span style="display: flex; align-items: center;justify-content: right;">{{ item.paramDesc }}</span>
                                            </div>

                                        </el-dropdown-item>
                                    </el-dropdown-menu>
                                </el-dropdown>


                                <el-button type="primary" size="mini" plain @click="handleCodeAreaEdit">{{
                                    item.editName }}</el-button>
                                <el-button type="primary" size="mini" plain @click="handleCodeAreaSave">保存</el-button>
                                <el-button type="primary" size="mini" plain @click="handleExecute">执行</el-button>
                                <el-button type="primary" size="mini" plain @click="handleRefresh">回刷</el-button>
                                <el-button type="primary" size="mini" plain @click="handlePublish">发布</el-button>
                            </span>
                        </div>

                        <!-- <div class="main-code-editor-area">
                            <el-input type="textarea" size="small" :autosize="{ minRows: 25 }"
                                v-model="item.dataDevelop.codeArea" autocomplete
                                :readonly="item.dataDevelop.isCodeAreaReadOnly">
                            </el-input>
                        </div> -->

                        <div class="main-code-editor-area" style="margin-top: 10px;">
                            <CodeEditor 
                                v-model="item.dataDevelop.codeArea" 
                                :language="item.dataDevelop.language"
                                :readOnly="item.dataDevelop.isCodeAreaReadOnly"
                                @input="changeTextarea"
                                style="height: 100%"
                            ></CodeEditor>
                        </div>

                        <div v-show="item.dataDevelop.isVisibleLogging" style="margin-top: 10px;">
                            <span>
                                执行日志
                            </span>
                            <el-input type="textarea" size="small" :autosize="{ minRows: 20 }"
                                v-model="item.dataDevelop.logArea" readonly>
                            </el-input>
                        </div>

                    </el-tab-pane>



                    <el-tab-pane label="调度配置" name="schedule-config">
                        <div class="schedule-config">
                            <div style="margin: 20px;"></div>
                            <el-form :label-position="item.scheduleConfig.labelPosition" label-width="100px"
                                :model="item.scheduleConfig" size="mini" style="width: 50%;">
                                <el-form-item label="调度系统">
                                    <el-input v-model="item.scheduleConfig.system" disabled></el-input>
                                </el-form-item>
                                <el-form-item label="调度项目">
                                    <el-input v-model="item.scheduleConfig.project" disabled></el-input>
                                </el-form-item>
                                <el-form-item label="工作流名称">
                                    <el-input v-model="item.scheduleConfig.workflowName" disabled></el-input>
                                </el-form-item>
                                <el-form-item label="节点名称">
                                    <el-input v-model="item.scheduleConfig.taskNodeName" disabled></el-input>
                                </el-form-item>
                                <el-form-item label="任务类型">
                                    <el-input v-model="item.scheduleConfig.taskType" disabled></el-input>
                                </el-form-item>
                                <el-form-item label="优先级">
                                    <el-input v-model="item.scheduleConfig.priority"></el-input>
                                </el-form-item>
                                <el-form-item label="重试次数">
                                    <el-input v-model="item.scheduleConfig.retryTimes"></el-input>
                                </el-form-item>
                                <el-form-item label="重试间隔">
                                    <el-input v-model="item.scheduleConfig.retryInterval"></el-input>
                                </el-form-item>
                                <el-form-item label="开始时间">
                                    <el-input v-model="item.scheduleConfig.beginTime"></el-input>
                                </el-form-item>

                                <el-form-item label="依赖任务">
                                    <el-divider></el-divider>
                                    <div style="display: flex;justify-content: end;">
                                        <el-button type="primary" size="mini" plain>依赖识别</el-button>
                                    </div>

                                    <el-table :data="item.scheduleConfig.dependency" style="width: 100%">
                                        <el-table-column label="上游依赖">
                                            <template slot-scope="scope">
                                                <el-popover trigger="hover" placement="top">
                                                    <div slot="reference" class="name-wrapper">
                                                        <el-tag size="medium">{{ scope.row.task }}</el-tag>
                                                    </div>
                                                </el-popover>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="操作">
                                            <template slot-scope="scope">
                                                <div
                                                    style="display: flex;align-items: center;justify-content: flex-end;">
                                                    <el-button size="mini"
                                                        @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                                                    <el-button size="mini" type="danger"
                                                        @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                                                </div>
                                            </template>
                                        </el-table-column>
                                    </el-table>
                                    <div
                                        style="display: flex;justify-content: end;margin-top: 10px;margin-right: 10px;">
                                        <el-button type="primary" size="mini" plain>添加</el-button>
                                    </div>
                                </el-form-item>

                            </el-form>

                            <div style="display: flex;justify-content: end;margin-right: 20px;">
                                <el-button type="primary" size="mini" plain>保存</el-button>
                            </div>

                            <el-divider content-position="left">在行调度列表</el-divider>

                            <el-descriptions title="T+1调度: dim.dim_date.day">
                                <el-descriptions-item label="调度系统">{{ item.scheduleConfig.system
                                    }}</el-descriptions-item>
                                <el-descriptions-item label="任务类型">{{ item.scheduleConfig.taskType
                                    }}</el-descriptions-item>
                                <el-descriptions-item label="优先级">{{ item.scheduleConfig.priority
                                    }}</el-descriptions-item>
                                <el-descriptions-item label="重试次数">{{
                                    item.scheduleConfig.retryTimes }}</el-descriptions-item>
                                <el-descriptions-item label="重试间隔(分)">{{
                                    item.scheduleConfig.retryInterval }}</el-descriptions-item>
                                <el-descriptions-item label="开始时间">{{ item.scheduleConfig.beginTime
                                    }}</el-descriptions-item>
                            </el-descriptions>

                            <div style="display: flex;justify-content: end;margin-right: 20px;">
                                <el-button type="primary" size="mini" plain>编辑</el-button>
                                <el-button type="danger" size="mini" plain>删除</el-button>
                            </div>

                        </div>
                    </el-tab-pane>


                    <el-tab-pane label="数据监控" name="dqc" disabled>

                    </el-tab-pane>


                    <el-tab-pane label="执行日志" name="running-log" disabled>

                    </el-tab-pane>


                </el-tabs>

            </el-tab-pane>

        </el-tabs>
    </div>
</template>

<script>
import CodeEditor from '../../common/CodeEditor.vue';
import { EventBus } from '../../common/event-bus';

export default {
    name: 'TaskManagerMain',
    components: {
        CodeEditor
    },
    data() {
        return {
            selectedTasks: [],
            activeTask: "",
            activeTabs: [],
            editableTabsValue: '1',
            editableTabs: [],
            tabIndex: 1,
            currentSelectedTab: {},
            paramList: []
        }
    },
    created() {
        EventBus.$on('tasks-activeTask', data => {
            this.activeTask = data;
        });
        EventBus.$on('tasks-selectedTasks', data => {
            this.selectedTasks = data;
        });
    },
    mounted() {
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
                    language:'sql',
                    codeArea: taskObject.taskScript,
                    logArea: "",
                    isCodeAreaReadOnly: true,
                    isVisibleLogging: false
                },
                scheduleConfig: {
                    labelPosition: 'right',
                    system: "DolphinScheduler",
                    project: "数仓调度项目",
                    workflowName:'',
                    taskNodeName:'',
                    taskType: "shell",
                    priority: "low",
                    retryTimes: "3",
                    retryInterval: "5",
                    beginTime: "00:15",
                    dependency: [
                        { task: 'ods.dim_date' },
                        { task: 'ods.dim_city' }
                    ]
                }
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
                    updatedUser: taskInfo.updatedUser,
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
                if (response.data.status === 'fail' && response.data.message === 'noChange') {
                    alert("您的任务没有变更!");
                }
            } catch (error) {
                console.error("Failed to load data:", error);
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
        clickTab(thisTab) {
            const currentTab = this.editableTabs.find(tab => tab.name === thisTab);
            this.currentSelectedTab = currentTab;
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
        handleExecute() {

        },
        handleRefresh() {

        },
        handlePublish() {

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