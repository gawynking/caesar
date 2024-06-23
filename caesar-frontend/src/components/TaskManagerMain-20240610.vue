<!-- src/components/Header.vue -->
<template>
    <div class="task-manager-main">
        <el-tabs v-model="activeTask" @tab-remove="removeTab">

            <el-tab-pane v-for="task in this.selectedTasks" :key="task" :label="task" :name="task" closable>
                <el-tabs v-model="activeSubTask">
                    <el-tab-pane label="数据开发" name="by-task-devolop">

                        <div class="main-code-editor-menu">
                            <span class="main-code-editor-menu-left">
                                <el-button type="primary" size="mini" plain>编辑</el-button>
                                <el-button type="primary" size="mini" plain>保存</el-button>
                                <el-button type="primary" size="mini" plain>历史</el-button>
                            </span>

                            <span class="main-code-editor-menu-right">
                                <el-button type="primary" size="mini" plain>执行</el-button>
                                <el-button type="primary" size="mini" plain>回刷</el-button>
                                <el-button type="primary" size="mini" plain>发布</el-button>
                            </span>
                        </div>

                        <div class="main-code-editor-area">
                            <el-input type="textarea" size="small" :autosize="{ minRows: 25 }" v-model="getTaskCodeArea"
                                autocomplete :readonly="getIsCodeAreaReadOnly"></el-input>
                        </div>

                        <div v-show="getIsVisibleLogging" style="margin-top: 10px;">
                            <span>
                                执行日志
                            </span>
                            <el-input type="textarea" size="small" :autosize="{ minRows: 20 }" v-model="getLogArea"
                                readonly></el-input>
                        </div>

                    </el-tab-pane>

                    <el-tab-pane label="调度管理" name="by-task-schedule">

                        <div class="schedule-config">
                            <div style="margin-top: 10px;"></div>

                            <el-form :label-position="labelPosition" label-width="100px" :model="getScheduleConfig" size="mini" style="width: 50%;">
                                <el-form-item label="调度系统">
                                    <el-input v-model="getScheduleConfig.system"></el-input>
                                </el-form-item>
                                <el-form-item label="任务类型">
                                    <el-input v-model="getScheduleConfig.taskType"></el-input>
                                </el-form-item>
                                <el-form-item label="优先级">
                                    <el-input v-model="getScheduleConfig.priority"></el-input>
                                </el-form-item>
                                <el-form-item label="重试次数">
                                    <el-input v-model="getScheduleConfig.retryTimes"></el-input>
                                </el-form-item>
                                <el-form-item label="重试间隔(分)">
                                    <el-input v-model="getScheduleConfig.retryInterval"></el-input>
                                </el-form-item>
                                <el-form-item label="开始时间">
                                    <el-input v-model="getScheduleConfig.beginTime"></el-input>
                                </el-form-item>

                                <el-form-item label="依赖任务">
                                    <el-divider></el-divider>
                                    <div style="display: flex;justify-content: end;">
                                        <el-button type="primary" size="mini" plain>依赖识别</el-button>
                                    </div>

                                    <el-table :data="getScheduleConfig.dependency" style="width: 100%">
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
                                <el-descriptions-item label="调度系统">{{ getScheduleConfig.system }}</el-descriptions-item>
                                <el-descriptions-item label="任务类型">{{ getScheduleConfig.taskType
                                    }}</el-descriptions-item>
                                <el-descriptions-item label="优先级">{{ getScheduleConfig.priority
                                    }}</el-descriptions-item>
                                <el-descriptions-item label="重试次数">{{ getScheduleConfig.retryTimes
                                    }}</el-descriptions-item>
                                <el-descriptions-item label="重试间隔(分)">{{ getScheduleConfig.retryInterval
                                    }}</el-descriptions-item>
                                <el-descriptions-item label="开始时间">{{ getScheduleConfig.beginTime
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
            activeTask: "",
            activeSubTask: "by-task-devolop",
            labelPosition: 'right',
            selectedTasks: [],
            taskArray: []
        }
    },
    created() {
        EventBus.$on('tasks-activeTask', data => {
            this.activeTask = data;
        });
        EventBus.$on('tasks-selectedTasks', data => {
            this.selectedTasks = data;
            this.updateTaskArray();
        });
    },
    watch: {
        selectedTasks(newSelectedTasks) {
            this.selectedTasks = newSelectedTasks;
            this.updateTaskArray();
        }
    },
    methods: {
        createTask(taskName) {
            this.taskMap[taskName] = {
                name: taskName,
                taskCodeArea: "",
                isCodeAreaReadOnly: true,
                isVisibleLogging: false,
                logArea: "",
                scheduleConfig: {
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
            };
            console.log("task ==> " + JSON.stringify(this.taskMap))
        },
        updateTaskArray() {
            for (const task of this.selectedTasks) {
                var tmp = false;
                for (const item of this.taskArray) {
                    if (task === item.name) {
                        tmp = true;
                    }
                }
                if (!tmp) {
                    this.taskArray.push({
                        "name": task,
                        taskCodeArea: "",
                        isCodeAreaReadOnly: false,
                        isVisibleLogging: false,
                        logArea: "",
                        scheduleConfig: {
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
                    })
                }
            }
        },
        removeTab(taskName) {
            this.selectedTasks = this.selectedTasks.filter(t => t !== taskName);
            this.taskArray = [];
            this.updateTaskArray();
            if (this.selectedTasks.length) {
                this.activeTab = '';
            } else {
                this.activeTab = '';
            }
        }
    },
    computed: {
        getTaskCodeArea: {
            get() {
                for (const item of this.taskArray) {
                    if (this.activeTask === item.name) {
                        return item.taskCodeArea;
                    }
                }
            },
            set(value) {
                for (const item of this.taskArray) {
                    if (this.activeTask === item.name) {
                        item.taskCodeArea = value;
                    }
                }
            }
        },
        getIsCodeAreaReadOnly: {
            get() {
                for (const item of this.taskArray) {
                    if (this.activeTask === item.name) {
                        return item.isCodeAreaReadOnly;
                    }
                }
            }
        },
        getScheduleConfig: {
            get() {
                for (const item of this.taskArray) {
                    if (this.activeTask === item.name) {
                        return item.scheduleConfig;
                    }
                }
            }
        },
        getLogArea: {
            get() {
                for (const item of this.taskArray) {
                    if (this.activeTask === item.name) {
                        return item.logArea;
                    }
                }
            }
        },
        getIsVisibleLogging: {
            get() {
                for (const item of this.taskArray) {
                    if (this.activeTask === item.name) {
                        return item.isVisibleLogging;
                    }
                }
            }
        }
    }
};
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
</style>