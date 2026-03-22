<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from './stores/useUserStore'
import { Bell, Search, ArrowDown, EditPen, ChatLineSquare, FolderOpened } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ref, onMounted, watch } from 'vue'
import { getUnreadCount } from './api/notification'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const searchText = ref('')
const unreadCount = ref(0)

const handleSearch = () => {
  if (searchText.value.trim()) {
    router.push({ path: '/', query: { tag: searchText.value.trim() } })
  }
}

const handleCommand = (command: string) => {
  if (command === 'profile') router.push('/profile')
  if (command === 'theme') userStore.toggleTheme()
  if (command === 'logout') {
    userStore.logoutUser().then(() => {
      ElMessage.success('已退出登录')
      router.push('/')
    })
  }
}

const handleCreatorCommand = (command: string) => {
  if (command === 'write') ElMessage.success('正在跳转到写文章页面...')
  if (command === 'pin') ElMessage.success('正在跳转到发沸点页面...')
  if (command === 'drafts') ElMessage.info('草稿箱功能即将上线')
}

const fetchUnreadCount = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.unreadCount
  } catch {
    // silent fail
  }
}

onMounted(() => {
  fetchUnreadCount()
})

// Re-fetch when login state changes — delay to let session cookie settle
watch(() => userStore.isLoggedIn, (val) => {
  if (val) {
    setTimeout(() => fetchUnreadCount(), 600)
  } else {
    unreadCount.value = 0
  }
})

</script>

<template>
  <div id="app" :class="{ 'dark-mode': userStore.isDark }">
    <!-- Header - hide on login page -->
    <header class="main-header" v-if="route.name !== 'login'">
      <div class="header-inner">
        <div class="header-left">
          <div class="logo" @click="router.push('/')">
            <el-icon :size="24"><Promotion /></el-icon>
            <span>Echo Community</span>
          </div>
          <nav class="main-nav">
            <router-link to="/" class="nav-item">首页</router-link>
            <router-link to="/column" class="nav-item">专栏</router-link>
            <router-link to="/interview" class="nav-item">面经</router-link>
            <router-link to="/moment" class="nav-item">沸点</router-link>
            <router-link to="/notifications" class="nav-item" v-if="userStore.isLoggedIn">通知</router-link>
            <router-link to="/messages" class="nav-item" v-if="userStore.isLoggedIn">私信</router-link>
          </nav>
        </div>

        <div class="header-right">
          <div class="search-wrapper">
            <el-input
              v-model="searchText"
              placeholder="搜索帖子/用户..."
              :prefix-icon="Search"
              class="header-search"
              @keyup.enter="handleSearch"
            />
          </div>
          
          <!-- Logged in -->
          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click" @command="handleCreatorCommand">
              <el-button type="primary" class="write-btn">
                创作者中心 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="write" :icon="EditPen">写文章</el-dropdown-item>
                  <el-dropdown-item command="pin" :icon="ChatLineSquare">发沸点</el-dropdown-item>
                  <el-dropdown-item command="drafts" :icon="FolderOpened" divided>草稿箱</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            
            <div class="action-icons">
              <el-badge :value="unreadCount || undefined" class="header-badge" type="danger">
                <el-icon :size="22" class="action-icon" @click="router.push('/notifications')"><Bell /></el-icon>
              </el-badge>
            </div>

            <el-dropdown trigger="click" @command="handleCommand">
              <el-avatar
                :size="36"
                :src="userStore.userInfo?.avatar || 'https://api.dicebear.com/7.x/bottts/svg?seed=Felix'"
                class="header-avatar"
              />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">我的主页</el-dropdown-item>
                  <el-dropdown-item command="theme">{{ userStore.isDark ? '浅色模式' : '深色模式' }}</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <!-- Not logged in -->
          <template v-else>
            <el-button type="primary" @click="router.push('/login')">登录 / 注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <main>
      <router-view />
    </main>
  </div>
</template>

<style>
/* ──── CSS Variables ──── */
:root {
  color-scheme: light;
  --bg-page: #f4f5f5;
  --card-bg: #fff;
  --border-color: #e4e6eb;
  --text-primary: #252933;
  --text-secondary: #515767;
  --text-tertiary: #8a919f;
  --juejin-blue: #1e80ff;
}
body {
  margin: 0;
  background-color: var(--bg-page);
  color: var(--text-primary);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
a { text-decoration: none; color: inherit; }

/* Layout */
.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 20px;
}
.content-area { flex: 1; min-width: 0; }
.sidebar-area { width: 300px; flex-shrink: 0; }

/* Panel */
.echo-panel {
  background: var(--card-bg);
  border-radius: 4px;
  margin-bottom: 8px;
  border: 1px solid var(--border-color);
}

/* List Item */
.list-item {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}
.list-item:last-child { border-bottom: none; }
.list-item:hover { background: rgba(30, 128, 255, 0.03); }

/* ──── Header ──── */
.main-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-color);
  height: 60px;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 100%;
  justify-content: space-between;
}
.header-left { display: flex; align-items: center; flex: 1; }
.header-right { display: flex; align-items: center; gap: 16px; flex-shrink: 0; }
.logo {
  display: flex; align-items: center; gap: 8px;
  font-size: 20px; font-weight: 600; color: var(--juejin-blue);
  cursor: pointer; user-select: none;
  flex-shrink: 0;
  margin-right: 40px;
}
.main-nav { display: flex; flex: 1; align-items: center; justify-content: space-evenly; margin: 0 40px; }
.nav-item {
  font-size: 15px; color: var(--text-secondary);
  padding: 4px 0; transition: color 0.2s;
  white-space: nowrap;
}
.nav-item:hover, .nav-item.router-link-active { color: var(--juejin-blue); font-weight: 500; }

.header-search { width: 240px; transition: width 0.3s; }
.write-btn { border-radius: 4px; }
.action-icons { display: flex; align-items: center; gap: 12px; }
.action-icon { cursor: pointer; color: var(--text-secondary); transition: color 0.2s; }
.action-icon:hover { color: var(--juejin-blue); }
.header-avatar { cursor: pointer; }
</style>
