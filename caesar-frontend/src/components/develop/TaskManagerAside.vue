<!-- src/components/TaskManagerAside.vue -->
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

                <el-menu-item v-for="grandChild in child.children" :key="grandChild.menuIndex"
                    :index="grandChild.menuIndex">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span>{{ grandChild.menuName }}</span>

                        <el-popover placement="top" width="160" v-model="grandChild.taskDeleteVisible">
                            <p>确定删除任务 {{ grandChild.menuName }} 吗？</p>
                            <div style="text-align: right; margin: 0">
                                <el-button size="mini" type="text"
                                    @click="grandChild.taskDeleteVisible = false">取消</el-button>
                                <el-button type="primary" size="mini"
                                    @click="sendDeleteSubtask(grandChild)">确定</el-button>
                            </div>
                            <el-button size="mini" type="text" slot="reference"
                                @click.native.prevent="deleteSubtask($event, grandChild)">
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
                        <el-option v-for="item in engines" :key="item.id" :label="item.engineType" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="用户组">
                    <el-select v-model="selectedGroup" placeholder="请选择用户组" @change="onGroupChange">
                        <el-option v-for="group in groups" :key="group.groupId" :label="group.groupName"
                            :value="group.groupId">
                        </el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="数据库名">
                    <el-select v-model="dbName" placeholder="请选择数据库">
                        <el-option v-for="item in filteredDbNames" :key="item.dbName" :label="item.dbName"
                            :value="item.dbName">
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
import { EventBus } from '../../common/event-bus';

export default {
    name: 'TaskManagerAside',
    data() {
        return {
            asideListData: [],
            loginUser: '',
            menuDbMapping: [],
            filterText: '',
            selectedTasks: [],
            dialogVisible: false,
            dbName: '',
            newTaskName: '',
            currentMenuItem: '',
            selectedGroup: null,
            engine: 102,
            engines: [],
            groups: [],
            taskDeleteVisible: false
        }
    },
    props: {
    },
    created() {
        EventBus.$on('login-user', data => {
            this.loginUser = sessionStorage.getItem('loginUser');
        });
        EventBus.$on('tasks-selectedTasks', data => {
            this.selectedTasks = data;
        });
        console.log("加载菜单数据 - created() " + sessionStorage.getItem('loginUser') + ".*")
        this.loadData(sessionStorage.getItem('loginUser')+".*");
        this.loadMenuDbs();
    },
    mounted() {
        this.loginUser = sessionStorage.getItem('loginUser');
        this.$nextTick(() => {
            this.syncFilterText();
        });
        this.loadEngines();
        this.loadBbs();
    },
    beforeMount() {
        this.syncFilterText();
    },
    watch: {
        filterText(newFilterText) {
            this.syncFilterText();
            console.log("加载菜单数据 - watch.filterText " + this.filterText)
            this.loadData(newFilterText);
        },
        selectedTasks(selectedTasks) {
            this.selectedTasks = selectedTasks;
        }
    },
    computed: {
        filteredDbNames() {
            if (this.selectedGroup === null) {
                return [];
            }
            const group = this.groups.find(g => g.groupId === this.selectedGroup);
            return group ? group.dbInfos : [];
        },
        filteredDbNames() {
            return this.menuDbMapping.filter(
                (item) => item.menuIndex === this.currentMenuItem.menuIndex
            );
        }
    },
    methods: {
        async loadMenuDbs() {
            try {
                const response = await this.$axios.get("/develop/getMenuDbs");
                if (response.data && response.data.status === 'success') {
                    this.menuDbMapping = response.data.data.items;
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        async loadData(filterText) {
            try {
                const response = await this.$axios.get("/develop/listTask", {
                    params: { partten: filterText }
                });
                console.log(' ==> ' + JSON.stringify(response.data.data.items))
                if (response.data && response.data.status === 'success') {
                    this.asideListData = response.data.data.items;
                    EventBus.$emit("asideListData", this.asideListData);
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        async loadEngines() {
            try {
                const response = await this.$axios.get("/develop/getEngines");
                if (response.data && response.data.status === 'success') {
                    this.engines = response.data.data.items;
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        async loadBbs() {
            try {
                const response = await this.$axios.get("/develop/getDbs");
                if (response.data && response.data.status === 'success') {
                    this.groups = response.data.data.items;
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        onGroupChange(groupId) {
            this.dbName = ''; // 清空之前选择的数据库名
        },
        syncFilterText() {
            EventBus.$on('filterText', data => {
                this.filterText = data
            })
        },
        addSubtask(event, child) {
            console.log(' ==> ' + JSON.stringify(child))
            event.stopPropagation();
            this.currentMenuItem = child;
            this.dialogVisible = true;
        },
        deleteSubtask(event, grandChild) {
            event.stopPropagation();
            this.currentMenuItem = grandChild;
        },
        async sendDeleteSubtask(grandChild) {
            try {
                const response = await this.$axios.get("/develop/deleteTask", {
                    params: { taskName: grandChild.menuName }
                });
                if (response.data && response.data.status === 'success') {
                    console.log('删除任务成功.')
                    console.log("加载菜单数据 - sendDeleteSubtask" + this.filterText)
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
            this.dialogVisible = false;
            try {
                const response = await this.$axios.post("/develop/addTask", {
                    menuIndex: this.currentMenuItem.menuIndex,
                    taskName: this.dbName + '.' + this.newTaskName,
                    engine: this.engine,
                    groupId: this.selectedGroup,
                    createdUserName: this.loginUser,
                    updatedUserName: this.loginUser
                });
                if (response.data && response.data.status === 'success') {
                    console.log('创建新任务成功.')
                    console.log("加载菜单数据 - submitTask" + this.filterText)
                    this.loadData(this.filterText);
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
            this.newTaskName = '';
            this.selectedGroup = '';
            this.dbName = '';
        },
        cancelTask() {
            this.dialogVisible = false;
            this.newTaskName = '';
            this.selectedGroup = '';
            this.dbName = '';
        }
    }
};
</script>

<style scoped></style>
