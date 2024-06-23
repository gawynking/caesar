<!-- src/components/Header.vue -->
<template>
    <fragment class="task-manager-aside">

        <el-submenu v-for="item in asideListData" :key="item.menuIndex" :index="item.menuIndex" v-if="!item.isLeaf">
            <template slot="title">
                <i class="el-icon-menu"></i>
                <span>{{ item.menuName }}</span>
            </template>

            <el-submenu v-for="child in item.children" :key="child.menuIndex" :index="child.menuIndex"
                v-if="!child.isLeaf">



                <template slot="title">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span>{{ child.menuName }}</span>
                        <el-button type="text" size="mini" style="margin-right: 10px;"
                            @click.native.prevent="addSubtask($event, child)">
                            <i class="el-icon-circle-plus-outline"></i>
                        </el-button>
                    </div>
                </template>


                <el-menu-item v-for="grandChild in child.children" :key="grandChild.menuIndex" :index="grandChild.menuIndex">
                    <!-- {{ grandChild.menuName }} -->
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span>{{ grandChild.menuName }}</span>

                        <!-- <el-button type="text" size="mini" style="margin-right: 10px;" @click.native.prevent="deleteSubtask($event, grandChild)">
                            <i class="el-icon-remove-outline"></i>
                        </el-button> -->

                        <el-popover placement="top" width="160" v-model="grandChild.taskDeleteVisible">
                            <p>确定删除任务 {{grandChild.menuName}} 吗？</p>
                            <div style="text-align: right; margin: 0">
                                <el-button size="mini" type="text" @click="grandChild.taskDeleteVisible = false">取消</el-button>
                                <el-button type="primary" size="mini" @click="sendDeleteSubtask(grandChild)">确定</el-button>
                            </div>
                            <el-button size="mini" type="text" slot="reference" @click.native.prevent="deleteSubtask($event, grandChild)">
                                <i class="el-icon-remove-outline"></i>
                            </el-button>
                        </el-popover>

                    </div>
                </el-menu-item>
            </el-submenu>
        </el-submenu>


        <el-dialog title="创建新的ETL任务" :visible.sync="dialogVisible" width="30%" :before-close="handleClose">
            <el-form>
                <el-form-item label="执行引擎">
                    <el-select v-model="engine" placeholder="请选择">
                        <el-option v-for="item in engines" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="任务名称">
                    <el-input v-model="newTaskName"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="cancelTask">取消</el-button>
                <el-button type="primary" @click="submitTask">确认</el-button>
            </div>
        </el-dialog>


    </fragment>
</template>


<script>
import { EventBus } from '../common/event-bus';

export default {
    name: 'TaskManagerAside',
    data() {
        return {
            currentLoginUser: 'GawynKing',
            asideListData: [],
            filterText: "",
            selectedTasks: [],
            dialogVisible: false,
            newTaskName: '',
            currentMenuItem: '',
            engine: 1,
            engines: [{
                value: 1,
                label: 'Hive'
            }, {
                value: 2,
                label: 'Spark'
            }, {
                value: 3,
                label: 'Flink'
            }, {
                value: 4,
                label: 'Doris'
            }, {
                value: 5,
                label: 'MySQL'
            }, {
                value: 6,
                label: 'Hbase'
            }
            ],
            taskDeleteVisible:false
        }
    },
    props: {
        // handleSelectTask: {
        //     type: Function,
        //     required: true
        // }
    },
    created() {
        EventBus.$on('tasks-selectedTasks', data => {
            if (typeof data !== 'string') {
                // 如果数据不是字符串，将其转换为字符串
                this.selectedTasks = JSON.stringify(data);
            } else {
                this.selectedTasks = data;
            }
        });
    },
    methods: {
        async loadData(filterText) {
            try {
                const response = await this.$axios.get("/task/listTask", {
                    params: { partten: filterText }
                });
                if (response.data && response.data.status === 'success') {
                    this.asideListData = response.data.data.items;
                    EventBus.$emit("asideListData", this.asideListData);
                    console.log(this.asideListData);
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        syncFilterText() {
            EventBus.$on('filterText', data => {
                this.filterText = data
            })
        },
        addSubtask(event, child) {
            event.stopPropagation();
            console.log("添加子任务 " + "     " + JSON.stringify(child))
            this.currentMenuItem = child;
            this.dialogVisible = true;
        },
        deleteSubtask(event, grandChild) {
            event.stopPropagation();
            this.currentMenuItem = grandChild;
            console.log("删除子任务请求 " + "     " + JSON.stringify(grandChild))
        },
        async sendDeleteSubtask(grandChild){
            console.log("删除子任务执行 " + "     " + JSON.stringify(grandChild))
            try {
                const response = await this.$axios.get("/task/deleteTask", {
                    params: { taskName: grandChild.menuName }
                });
                if (response.data && response.data.status === 'success') {
                    console.log('删除任务成功.')
                    this.loadData(this.filterText);
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
            this.newTaskName = '';
        },
        handleClose(done) {
            this.$confirm('确认关闭？')
                .then(_ => {
                    done();
                })
                .catch(_ => { });
        },
        async submitTask() {
            // 在这里处理新任务的逻辑
            console.log('New task name:', this.newTaskName);
            this.dialogVisible = false;
            // 调用后端插入数据 
            try {
                const response = await this.$axios.post("/task/addTask", {
                    menuIndex: this.currentMenuItem.menuIndex,
                    taskType: 1,
                    taskName: this.newTaskName,
                    execEngine: this.engine,
                    createdUserName: this.currentLoginUser,
                    updatedUserName: this.currentLoginUser
                });
                if (response.data && response.data.status === 'success') {
                    console.log('创建新任务成功.')
                    this.loadData(this.filterText);
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
            this.newTaskName = '';
        },
        cancelTask() {
            this.dialogVisible = false;
            this.newTaskName = '';
        }
    },
    beforeMount() {
        this.syncFilterText();
        this.loadData(this.filterText);
    },
    watch: {
        filterText(newFilterText) {
            this.syncFilterText();
            this.loadData(newFilterText);
        },
        selectedTasks(newSelectedTasks) {
            if (typeof newSelectedTasks !== 'string') {
                // 如果数据不是字符串，将其转换为字符串
                this.selectedTasks = JSON.stringify(newSelectedTasks);
            } else {
                this.selectedTasks = newSelectedTasks;
            }
        }
    },
    mounted() {
        // this.syncFilterText();
        // this.loadData(this.filterText);

        // Wait for next DOM update cycle to ensure filterText is ready
        this.$nextTick(() => {
            this.syncFilterText();
            this.loadData(this.filterText);
        });
    }
};
</script>

<style scoped></style>
