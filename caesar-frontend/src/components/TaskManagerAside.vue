<!-- src/components/Header.vue -->
<template>
    <fragment class="task-manager-aside">

        <el-submenu v-for="item in asideListData" :key="item.menuIndex" :index="item.menuIndex" v-if="!item.isLeaf">
            <template slot="title">
                <i class="el-icon-menu"></i>
                <span>{{ item.menuName }}</span>
            </template>

            <el-submenu v-for="child in item.children" :key="child.menuIndex" :index="child.menuIndex" v-if="!child.isLeaf">
                <template slot="title">{{ child.menuName }}</template>
                <el-menu-item v-for="grandChild in child.children" :key="grandChild.menuIndex" :index="grandChild.menuIndex">
                    {{ grandChild.menuName }}
                </el-menu-item>
            </el-submenu>
        </el-submenu>

    </fragment>
</template>


<script>
import { EventBus } from '../common/event-bus';

export default {
    name: 'TaskManagerAside',
    data() {
        return {
            asideListData: [],
            filterText:"",
            selectedTasks:[]
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
                    EventBus.$emit("asideListData",this.asideListData);
                    console.log(this.asideListData);
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }
        },
        syncFilterText(){
            EventBus.$on('filterText', data => {
                this.filterText = data
            })
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
        selectedTasks(newSelectedTasks){
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

<style scoped>
</style>
