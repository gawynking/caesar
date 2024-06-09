<!-- src/components/Header.vue -->
<template>
    <div class="task-manager-main">

        <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="dim.dim_date" name="first" closable>
                <el-tabs v-model="activeName" @tab-click="handleClick">
                    <el-tab-pane label="数据开发" name="first">

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

                        <!-- 富文本编辑区域 -->
                        <div class="main-code-editor-area">
                            <!-- <code-editor v-model="code"></code-editor> -->
                            <el-input type="textarea" size="small" :autosize="{ minRows: 25 }" v-model="codeArea"
                                autocomplete :readonly="isCodeAreaReadOnly">
                            </el-input>
                        </div>

                        <div v-show="isVisibleLogging" style="margin-top: 10px;">
                            <span>
                                执行日志
                            </span>
                            <el-input type="textarea" size="small" :autosize="{ minRows: 20 }" v-model="logArea"
                                readonly>
                            </el-input>
                        </div>

                    </el-tab-pane>
                    <el-tab-pane label="任务信息" name="task-info"></el-tab-pane>
                    <el-tab-pane label="指标定义" name="index-define"></el-tab-pane>
                    <el-tab-pane label="调度管理" name="schedule-manager">

                        <div class="schedule-config">
                            <!-- <el-divider content-position="left">调度配置</el-divider> -->
                            <!-- <el-button type="primary" size="mini" plain>添加</el-button> -->
                            <div style="margin-top: 10px;"></div>

                            <el-form :label-position="labelPosition" label-width="100px" :model="scheduleConfig"
                                size="mini" style="width: 50%;">
                                <el-form-item label="调度系统">
                                    <el-input v-model="scheduleConfig.system"></el-input>
                                </el-form-item>
                                <el-form-item label="任务类型">
                                    <el-input v-model="scheduleConfig.taskType"></el-input>
                                </el-form-item>
                                <el-form-item label="优先级">
                                    <el-input v-model="scheduleConfig.priority"></el-input>
                                </el-form-item>
                                <el-form-item label="重试次数">
                                    <el-input v-model="scheduleConfig.retryTimes"></el-input>
                                </el-form-item>
                                <el-form-item label="重试间隔(分)">
                                    <el-input v-model="scheduleConfig.retryInterval"></el-input>
                                </el-form-item>
                                <el-form-item label="开始时间">
                                    <el-input v-model="scheduleConfig.beginTime"></el-input>
                                </el-form-item>

                                <el-form-item label="依赖任务">
                                    <el-divider></el-divider>
                                    <div style="display: flex;justify-content: end;">
                                        <el-button type="primary" size="mini" plain>依赖识别</el-button>
                                    </div>

                                    <el-table :data="scheduleConfig.dependency" style="width: 100%">
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
                                                <div style="display: flex;align-items: center;justify-content: flex-end;">
                                                    <el-button size="mini"
                                                        @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                                                    <el-button size="mini" type="danger"
                                                        @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                                                </div>
                                            </template>
                                        </el-table-column>
                                    </el-table>
                                    <div style="display: flex;justify-content: end;margin-top: 10px;margin-right: 10px;">
                                        <el-button type="primary" size="mini" plain>添加</el-button>
                                    </div>
                                </el-form-item>
                            </el-form>

                            <div style="display: flex;justify-content: end;margin-right: 20px;">
                                <el-button type="primary" size="mini" plain>保存</el-button>
                            </div>


                            <el-divider content-position="left">在行调度列表</el-divider>

                            <el-descriptions title="T+1调度: dim.dim_date.day">
                                <el-descriptions-item label="调度系统">{{ scheduleConfig.system }}</el-descriptions-item>
                                <el-descriptions-item label="任务类型">{{ scheduleConfig.taskType }}</el-descriptions-item>
                                <el-descriptions-item label="优先级">{{ scheduleConfig.priority }}</el-descriptions-item>
                                <el-descriptions-item label="重试次数">{{ scheduleConfig.retryTimes}}</el-descriptions-item>
                                <el-descriptions-item label="重试间隔(分)">{{ scheduleConfig.retryInterval}}</el-descriptions-item>
                                <el-descriptions-item label="开始时间">{{ scheduleConfig.beginTime }}</el-descriptions-item>
                            </el-descriptions>

                            <div style="display: flex;justify-content: end;margin-right: 20px;">
                                <el-button type="primary" size="mini" plain>编辑</el-button>
                                <el-button type="danger" size="mini" plain>删除</el-button>
                            </div>

                        </div>

                    </el-tab-pane>
                </el-tabs>
            </el-tab-pane>

            <el-tab-pane label="dim.dim_city" name="second1" closable>
                任务信息
            </el-tab-pane>
        </el-tabs>


    </div>
</template>


<script>
import CodeEditor from '@/components/CodeEditor.vue';

export default {
    name: 'TaskManagerMain',
    components: {
        CodeEditor
    },
    data() {
        return {
            code: "",
            codeArea: "",
            isCodeAreaReadOnly: false,
            logArea: "",
            activeName: "first",
            isVisibleLogging: false,
            labelPosition: 'right',
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
            },
            dependencyConfig: {
                taskName: "dim.dim_date"
            }
        }
    },
    methods: {
        handleClick(){

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