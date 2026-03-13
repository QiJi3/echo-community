<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from './stores/useUserStore'
import { Bell, Search, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/', query: { tag: searchKeyword.value } })
  }
}

const handleCommand = (command: string) => {
  if (command === 'profile') router.push('/profile')
  if (command === 'theme') userStore.toggleTheme()
  if (command === 'logout') {
    ElMessage.success('已退出登录')
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
          <router-link to="/notifications" class="nav-item" active-class="active">通知</router-link>
          <router-link to="/messages" class="nav-item" active-class="active">私信</router-link>
        </nav>
      </div>
      
      <div class="header-right">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索帖子/用户..."
            :prefix-icon="Search"
            class="compact-search"
            @keyup.enter="handleSearch"
          />
        </div>
        
        <el-button type="primary" class="write-btn" @click="ElMessage.info('创作者中心正在建设中...')">
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

  <main class="main-body">
    <router-view />
  </main>
</template>

<style scoped>
.main-header {
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
  height: 60px;
}

html.dark .main-header {
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-color);
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
  color: var(--juejin-blue);
}

.main-nav {
  display: flex;
  height: 100%;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 0 16px;
  color: var(--text-secondary);
  font-size: 15px;
  height: 100%;
  position: relative;
}

.nav-item:hover {
  color: var(--text-primary);
}

.nav-item.active {
  color: var(--juejin-blue);
}

html.dark .nav-item { color: var(--text-secondary); }
html.dark .nav-item.active { color: var(--juejin-blue); }

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  width: 240px;
}

:deep(.compact-search .el-input__wrapper) {
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  box-shadow: none;
  border-radius: 4px;
}

:deep(.compact-search .el-input__wrapper.is-focus) {
  background-color: var(--card-bg);
  border-color: var(--juejin-blue);
}

html.dark :deep(.compact-search .el-input__wrapper) {
  background-color: var(--bg-color);
}

html.dark :deep(.compact-search .el-input__wrapper.is-focus) {
  background-color: var(--card-bg);
}

.write-btn {
  background-color: var(--juejin-blue);
  border-color: var(--juejin-blue);
  border-radius: 4px;
}

.action-icons {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-btn {
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
}

.icon-btn:hover {
  color: var(--text-secondary);
}

html.dark .icon-btn { color: var(--text-tertiary); }

.avatar-wrapper {
  cursor: pointer;
  margin-left: 8px;
}
</style>
