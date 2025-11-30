<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import { useUserStore } from './stores/useUserStore'
import { Bell, Edit, Search, ArrowDown } from '@element-plus/icons-vue'
import { ref } from 'vue'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')

const handleCommand = (command: string) => {
  if (command === 'profile') router.push('/profile')
  if (command === 'theme') userStore.toggleTheme()
  if (command === 'logout') {
    router.push('/')
  }
}
</script>

<template>
  <header class="main-header">
    <div class="header-inner">
      <div class="header-left">
        <a href="/" class="logo-link" @click.prevent="router.push('/')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="#1e80ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="#1e80ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="#1e80ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span class="logo-text">Echo Community</span>
        </a>
        <nav class="main-nav">
          <router-link to="/" class="nav-item" active-class="active">首页</router-link>
          <router-link to="/hot" class="nav-item">沸点</router-link>
          <router-link to="/course" class="nav-item">课程</router-link>
        </nav>
      </div>
      
      <div class="header-right">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索帖子/用户..."
            :prefix-icon="Search"
            class="compact-search"
          />
        </div>
        
        <el-button type="primary" class="write-btn">
          创作者中心 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </el-button>
        
        <div class="action-icons">
          <el-badge :value="3" class="header-badge" type="danger">
            <div class="icon-btn" @click="router.push('/notifications')">
              <el-icon :size="20"><Bell /></el-icon>
            </div>
          </el-badge>
        </div>
        
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="avatar-wrapper">
            <el-avatar :size="32" :src="userStore.userInfo.avatar" />
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">我的主页</el-dropdown-item>
              <el-dropdown-item command="theme">切换模式 ({{ userStore.isDark ? '亮色' : '暗色' }})</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>

  <router-view />
</template>

<style scoped>
.main-header {
  background: #ffffff;
  border-bottom: 1px solid #f1f2f3;
  position: sticky;
  top: 0;
  z-index: 100;
  height: 60px;
}

html.dark .main-header {
  background: #1e1e1e;
  border-bottom: 1px solid #2d2d2d;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  height: 100%;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 24px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #1e80ff; /* Juejin blue */
}

.main-nav {
  display: flex;
  height: 100%;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 0 16px;
  color: #515767;
  font-size: 15px;
  height: 100%;
  position: relative;
}

.nav-item:hover {
  color: #1e80ff;
}

.nav-item.active {
  color: #1e80ff;
}

html.dark .nav-item { color: #a3a6ad; }
html.dark .nav-item.active { color: #1e80ff; }

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  width: 240px;
}

:deep(.compact-search .el-input__wrapper) {
  background-color: #f2f3f5;
  border: 1px solid transparent;
  box-shadow: none;
  border-radius: 4px;
}

:deep(.compact-search .el-input__wrapper.is-focus) {
  background-color: #ffffff;
  border-color: #1e80ff;
}

html.dark :deep(.compact-search .el-input__wrapper) {
  background-color: #2b2b2b;
}

html.dark :deep(.compact-search .el-input__wrapper.is-focus) {
  background-color: #1e1e1e;
}

.write-btn {
  background-color: #1e80ff;
  border-color: #1e80ff;
  border-radius: 4px;
}

.action-icons {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-btn {
  color: #8a919f;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.icon-btn:hover {
  color: #515767;
}

html.dark .icon-btn { color: #a3a6ad; }

.avatar-wrapper {
  cursor: pointer;
  margin-left: 8px;
}
</style>
