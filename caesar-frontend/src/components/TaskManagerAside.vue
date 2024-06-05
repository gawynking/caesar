<!-- src/components/Header.vue -->
<template>
    <fragment class="task-manager-aside">

        <el-submenu
            v-for="item in asideListData"
            :key="item.menuIndex"
            :index="item.menuIndex"
            v-if="!item.isLeaf"
        >
            <template slot="title">
                <i class="el-icon-menu"></i>
                <span>{{ item.menuName }}</span>
            </template>

            <el-submenu
                v-for="child in item.children"
                :key="child.menuIndex"
                :index="child.menuIndex"
                v-if="!child.isLeaf"
            >
                <template slot="title">{{ child.menuName }}</template>
                <el-menu-item
                    v-for="grandChild in child.children"
                    :key="grandChild.menuIndex"
                    :index="grandChild.menuIndex"
                >
                    {{ grandChild.menuName }}
                </el-menu-item>
            </el-submenu>
        </el-submenu>

    </fragment>
</template>

<script>
export default {
    name: 'TaskManagerAside',
    data() {
        return {
            asideListData:[]
        }
    },
    methods: {
        async loadData(){
            try {
                const response = await this.$axios.get("/task/listTask");
                if (response.data && response.data.status === 'success') {
                    this.asideListData = response.data.data.items;
                    console.log(this.asideListData)
                }
            } catch (error) {
                console.error("Failed to load data:", error);
            }

        }
    },
    beforeMount(){
        this.loadData();
    }
};
</script>

<style scoped>
</style>