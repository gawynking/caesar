<template>
    <div class="login-container">
        <div class="login" v-if="isLogin">
            <el-form :model="loginForm" @submit.native.prevent="handleLogin" ref="loginForm" label-width="100px">
                <el-form-item label="用户名: " prop="username">
                    <el-input v-model="loginForm.username" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="密码: " prop="password">
                    <el-input type="password" v-model="loginForm.password" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleLogin">登录</el-button>
                    <el-button type="primary" @click="toggleForm" plain>注册</el-button>
                </el-form-item>
            </el-form>
        </div>
        
        <div class="register" v-else>
            <el-form :model="registerForm" @submit.native.prevent="handleRegister" ref="registerForm" :rules="rules" label-width="100px">
                <el-form-item label="用户名: " prop="username">
                    <el-input v-model="registerForm.username" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="密码: " prop="password">
                    <el-input type="password" v-model="registerForm.password" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="邮箱: " prop="email">
                    <el-input v-model="registerForm.email" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="手机号: " prop="phone">
                    <el-input v-model="registerForm.phone" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="团队: " prop="teamGroup">
                    <el-select v-model="registerForm.teamGroup" placeholder="请选择">
                        <el-option
                        v-for="item in teamList"
                        :key="item.id"
                        :label="item.groupName"
                        :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleRegister">注册</el-button>
                    <el-button @click="toggleForm" plain>登录</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
import { EventBus } from '../common/event-bus';

export default {
    data() {
        return {
            loginUser:'admin',
            isLogin: true,
            loginForm: {
                username: '',
                password: ''
            },
            registerForm: {
                username: '',
                password: '',
                email: '',
                phone: '',
                teamGroup: ''
            },
            teamList:[],
            rules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                ],
                email: [
                    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
                    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
                ],
                phone: [
                    { required: true, message: '请输入手机号', trigger: 'blur' },
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }
                ],
                teamGroup: [
                    { required: true, message: '请选择所属团队', trigger: 'blur' },
                ]
            }
        };
    },
    mounted() {
        this.getTeamList();
    },
    methods: {
        toggleForm() {
            this.isLogin = !this.isLogin;
        },
        handleLogin() {
            this.$refs.loginForm.validate((valid) => {
                if (valid) {
                    this.$axios.post('/auth/login', {
                        username: this.loginForm.username,
                        password: this.loginForm.password
                    }).then(response => {
                        if(response.data.code === 200){
                            const token = response.data.data.items;
                            this.loginUser = this.loginForm.username;
                            sessionStorage.setItem('token', token);
                            sessionStorage.setItem('loginUser', this.loginUser);
                            EventBus.$emit('login-user',this.loginUser);
                            this.$router.push({ name: 'Home' });
                        } else {
                            this.$message.error('登录失败，请检查用户名和密码');
                        }
                    }).catch(error => {
                        console.error('登录失败:', error);
                        this.$message.error('登录失败，请检查用户名和密码');
                    });
                } else {
                    console.log('表单验证失败');
                    return false;
                }
            });
        },
        handleRegister() {
            this.$refs.registerForm.validate((valid) => {
                if (valid) {
                    this.$axios.post('/auth/register', {
                        username: this.registerForm.username,
                        password: this.registerForm.password,
                        email: this.registerForm.email,
                        phone: this.registerForm.phone,
                        teamGroup: this.registerForm.teamGroup
                    }).then(response => {
                        if(response.data.code === 200){
                            this.toggleForm();
                            alert('注册成功，请联系管理员审核');
                        } else {
                            this.$message.error('注册失败，请重试');
                        }
                    }).catch(error => {
                        console.error('注册失败:', error);
                        this.$message.error('注册失败，请重试');
                    });
                } else {
                    console.log('表单验证失败');
                    return false;
                }
            });
        },
        async getTeamList() {
            try {
                const response = await this.$axios.get('/auth/getTeamList');
                if (response.data.code === 200) {
                    this.teamList = response.data.data.items;
                } else {
                    this.$message.error('获取团队列表失败');
                }
            } catch (error) {
                console.error('获取团队列表失败:', error);
                this.$message.error('获取团队列表失败');
            }
        }

    }
};
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.login, .register {
    max-width: 300px;
    width: 100%;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    background: #fff;
    border-radius: 8px;
}
</style>
