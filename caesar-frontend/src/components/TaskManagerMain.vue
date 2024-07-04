<!-- src/components/Header.vue -->
<template>
    <div class="task-manager-main">

        <el-tabs v-model="editableTabsValue" type="card" closable @tab-remove="removeTab">

            <el-tab-pane v-for="(item, index) in editableTabs" :key="item.name" :label="item.title" :name="item.name">

                <el-tabs v-model="item.activeName" class="indented-tab">
                    <el-tab-pane label="数据开发" name="data-develop">

                        <div class="main-code-editor-menu">
                            <span class="main-code-editor-menu-left">

                                <el-button type="primary" size="mini" plain>历史</el-button>
                            </span>

                            <span class="main-code-editor-menu-right">
                                <el-button type="primary" size="mini" plain>编辑</el-button>
                                <el-button type="primary" size="mini" plain>保存</el-button>
                                <el-button type="primary" size="mini" plain>执行</el-button>
                                <el-button type="primary" size="mini" plain>回刷</el-button>
                                <el-button type="primary" size="mini" plain>发布</el-button>
                            </span>
                        </div>

                        <div class="main-code-editor-area">
                            <el-input type="textarea" size="small" :autosize="{ minRows: 25 }"
                                v-model="item.dataDevelop.codeArea" autocomplete
                                :readonly="item.dataDevelop.isCodeAreaReadOnly">
                            </el-input>
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
                                <el-form-item label="名称">
                                    <el-input v-model="item.scheduleConfig.system"></el-input>
                                </el-form-item>
                                <el-form-item label="任务类型">
                                    <el-input v-model="item.scheduleConfig.taskType"></el-input>
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

                </el-tabs>
            </el-tab-pane>

        </el-tabs>
    </div>
</template>

<script>
import CodeEditor from '@/components/CodeEditor.vue';
import { EventBus } from '../common/event-bus';

export default {
    name: 'TaskManagerMain',
    components: {
        CodeEditor
    },
    data() {
        return {
            selectedTasks: [],
            activeTask: "",
            activeTabs:[],
            editableTabsValue: '1',
            editableTabs: [],
            tabIndex: 1
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
    watch: {
        activeTask(newActiveTask) {
            this.activeTask = newActiveTask;
            const index = this.selectedTasks.indexOf(this.activeTask);
            const flag = this.activeTabs.indexOf(index+'');
            if (flag === -1) {
                this.addTab(this.activeTask,index+'')
                this.activeTabs.push(index+'')
            }
            console.log('index = ' + index + '; flag ' + flag +  '; task = ' + this.activeTask + ';' + 'taskList = ' + this.selectedTasks + '; activeTabs ' + this.activeTabs)
        },
        selectedTasks(newSelectedTasks) {
            this.selectedTasks = newSelectedTasks;
        }
    },
    methods: {
        // targetName = index+'' 
        addTab(titleName,targetName) {
            // let newTabName = ++this.tabIndex + '';
            this.editableTabs.push({
                title: titleName,
                name: targetName,
                activeName: "data-develop",
                dataDevelop: {
                    codeArea: "",
                    logArea: "",
                    isCodeAreaReadOnly: false,
                    isVisibleLogging: false
                },
                scheduleConfig: {
                    labelPosition: 'right',
                    system: "DolphinScheduler",
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

            console.log('index = ' + targetName +  '; task = ' + this.activeTask + ';' + 'taskList = ' + this.selectedTasks + '; activeTabs ' + this.activeTabs)
        },
        removeTab(targetName) {
            console.log('将要删除的Tabs页签为: ' + targetName)
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
            console.log('index = ' + targetName +  '; task = ' + this.activeTask + ';' + 'taskList = ' + this.selectedTasks + '; activeTabs ' + this.activeTabs)
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
</style>