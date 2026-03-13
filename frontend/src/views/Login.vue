<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/useUserStore'
import { getCaptcha } from '../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeTab = ref<'login' | 'register'>('login')
const loginLoading = ref(false)
const registerLoading = ref(false)

// Captcha
const captchaImg = ref('')
const captchaId = ref('')

const loadCaptcha = async () => {
  try {
    const data = await getCaptcha()
    captchaId.value = data.captchaId
    captchaImg.value = data.imageBase64
  } catch {
    ElMessage.error('验证码加载失败')
  }
}

// ===== Login =====
const loginForm = reactive({
  username: '',
  password: '',
  captchaCode: '',
  rememberMe: false
})
const loginErrors = reactive({ username: '', password: '', captchaCode: '' })

const validateLoginField = (field: 'username' | 'password' | 'captchaCode') => {
  if (field === 'username') {
    loginErrors.username = loginForm.username.trim() ? '' : '请输入用户名'
  } else if (field === 'password') {
    loginErrors.password = loginForm.password ? '' : '请输入密码'
  } else if (field === 'captchaCode') {
    loginErrors.captchaCode = loginForm.captchaCode.trim() ? '' : '请输入验证码'
  }
}

const handleLogin = async () => {
  validateLoginField('username')
  validateLoginField('password')
  validateLoginField('captchaCode')
  if (loginErrors.username || loginErrors.password || loginErrors.captchaCode) return

  loginLoading.value = true
  try {
    await userStore.login({
      username: loginForm.username.trim(),
      password: loginForm.password,
      captchaId: captchaId.value,
      captchaCode: loginForm.captchaCode.trim(),
      rememberMe: loginForm.rememberMe
    })
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch {
    loginForm.captchaCode = ''
    loadCaptcha()
  } finally {
    loginLoading.value = false
  }
}

// ===== Register =====
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})
const regErrors = reactive({ username: '', password: '', confirmPassword: '', email: '' })

const validateRegField = (field: keyof typeof regErrors) => {
  if (field === 'username') {
    const u = registerForm.username.trim()
    if (!u) regErrors.username = '请输入用户名'
    else if (u.length < 3) regErrors.username = '用户名至少 3 个字符'
    else if (u.length > 32) regErrors.username = '用户名最多 32 个字符'
    else if (!/^[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(u)) regErrors.username = '用户名只能包含字母、数字、下划线或中文'
    else regErrors.username = ''
  } else if (field === 'email') {
    const e = registerForm.email.trim()
    if (!e) regErrors.email = '请输入邮箱'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(e)) regErrors.email = '邮箱格式不正确'
    else regErrors.email = ''
  } else if (field === 'password') {
    const p = registerForm.password
    if (!p) regErrors.password = '请输入密码'
    else if (p.length < 8) regErrors.password = '密码长度不能少于 8 位'
    else if (p.length > 64) regErrors.password = '密码长度不能超过 64 位'
    else regErrors.password = ''
    // Re-validate confirm if already filled
    if (registerForm.confirmPassword) validateRegField('confirmPassword')
  } else if (field === 'confirmPassword') {
    if (!registerForm.confirmPassword) regErrors.confirmPassword = '请确认密码'
    else if (registerForm.confirmPassword !== registerForm.password) regErrors.confirmPassword = '两次输入的密码不一致'
    else regErrors.confirmPassword = ''
  }
}

const handleRegister = async () => {
  validateRegField('username')
  validateRegField('email')
  validateRegField('password')
  validateRegField('confirmPassword')
  if (regErrors.username || regErrors.email || regErrors.password || regErrors.confirmPassword) return

  registerLoading.value = true
  try {
    await userStore.registerUser({
      username: registerForm.username.trim(),
      password: registerForm.password,
      email: registerForm.email.trim()
    })
    ElMessage.success('注册成功，已自动登录')
    router.push('/')
  } catch {
    // error displayed by interceptor (e.g. "用户名已被注册")
  } finally {
    registerLoading.value = false
  }
}

onMounted(() => {
  loadCaptcha()
})

// Switch tab → reload captcha
watch(activeTab, (tab) => {
  if (tab === 'login') loadCaptcha()
})
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <h1>Echo Community</h1>
        <p>技术社区 · 分享与交流</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form @submit.prevent="handleLogin" class="auth-form">
            <el-form-item :error="loginErrors.username">
              <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" size="large"
                @blur="validateLoginField('username')" />
            </el-form-item>
            <el-form-item :error="loginErrors.password">
              <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" size="large"
                show-password @blur="validateLoginField('password')" />
            </el-form-item>
            <el-form-item :error="loginErrors.captchaCode">
              <div class="captcha-row">
                <el-input v-model="loginForm.captchaCode" placeholder="验证码" size="large" class="captcha-input"
                  @keyup.enter="handleLogin" @blur="validateLoginField('captchaCode')" />
                <img v-if="captchaImg" :src="captchaImg" class="captcha-img" @click="loadCaptcha" title="点击刷新验证码" />
                <el-button v-else link @click="loadCaptcha" class="captcha-reload">加载验证码</el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="loginForm.rememberMe">7 天免登录</el-checkbox>
            </el-form-item>
            <el-button type="primary" size="large" class="submit-btn" @click="handleLogin"
              :loading="loginLoading">登 录</el-button>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form @submit.prevent="handleRegister" class="auth-form">
            <el-form-item :error="regErrors.username">
              <el-input v-model="registerForm.username" placeholder="用户名 (3-32 个字符)" prefix-icon="User" size="large"
                @blur="validateRegField('username')" />
            </el-form-item>
            <el-form-item :error="regErrors.email">
              <el-input v-model="registerForm.email" placeholder="邮箱" prefix-icon="Message" size="large"
                @blur="validateRegField('email')" />
            </el-form-item>
            <el-form-item :error="regErrors.password">
              <el-input v-model="registerForm.password" type="password" placeholder="密码 (至少 8 位)" prefix-icon="Lock"
                size="large" show-password @blur="validateRegField('password')" />
            </el-form-item>
            <el-form-item :error="regErrors.confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock"
                size="large" show-password @blur="validateRegField('confirmPassword')" />
            </el-form-item>
            <el-button type="primary" size="large" class="submit-btn" @click="handleRegister"
              :loading="registerLoading">注 册</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  padding: 40px 36px;
}

html.dark .login-card {
  background: #1e1e1e;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1e80ff;
  margin: 0 0 8px 0;
}

.login-header p {
  color: #8a919f;
  font-size: 14px;
  margin: 0;
}

.login-tabs {
  margin-top: 10px;
}

:deep(.login-tabs .el-tabs__item) {
  font-size: 16px;
}

.auth-form {
  margin-top: 20px;
}

.captcha-row {
  display: flex;
  gap: 12px;
  width: 100%;
}

.captcha-input {
  flex: 1;
}

.captcha-img {
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
}

.captcha-reload {
  height: 40px;
  white-space: nowrap;
}

.submit-btn {
  width: 100%;
  margin-top: 10px;
  font-size: 16px;
}
</style>
