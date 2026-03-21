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
/* 与 style.css 中的变量保持同步并删除此处冗余定义 */

/* Layout */
.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
  display: flex;
  gap: 24px;
}
.content-area { flex: 1; min-width: 0; }
.sidebar-area { width: 300px; flex-shrink: 0; }

/* Panel (在全局 style.css 中已增加平滑卡片样式) */
.echo-panel {
  background: var(--card-bg);
  border-radius: var(--radius-md);
  margin-bottom: 20px;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition-normal), transform var(--transition-normal);
}
.echo-panel:hover {
  box-shadow: var(--shadow-hover);
  transform: translateY(-2px);
}

/* List Item */
.list-item {
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  transition: background-color var(--transition-fast);
}
.list-item:last-child { border-bottom: none; }
.list-item:hover { background: var(--juejin-blue-light); }

/* ──── Header ──── */
.main-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.85); /* 毛玻璃背景 */
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-color);
  height: 64px;
}
html.dark .main-header {
  background: rgba(30, 41, 59, 0.85);
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
.header-right { display: flex; align-items: center; gap: 20px; flex-shrink: 0; }

.logo {
  display: flex; align-items: center; gap: 8px;
  font-size: 22px; font-weight: 700; color: var(--juejin-blue);
  cursor: pointer; user-select: none;
  flex-shrink: 0;
  margin-right: 48px;
  letter-spacing: -0.5px;
}

.main-nav { display: flex; flex: 1; align-items: center; gap: 12px; margin: 0 20px; }
.nav-item {
  font-size: 15px; color: var(--text-secondary);
  padding: 8px 16px; transition: all var(--transition-fast);
  white-space: nowrap;
  border-radius: var(--radius-sm);
  font-weight: 500;
}
.nav-item:hover { 
  color: var(--juejin-blue); 
  background: var(--juejin-blue-light); 
}
.nav-item.router-link-active { 
  color: var(--juejin-blue); 
  font-weight: 600; 
  background: var(--juejin-blue-light);
}

.header-search { width: 280px; transition: width var(--transition-normal); box-shadow: var(--shadow-sm); border-radius: 100px; }
.header-search :deep(.el-input__wrapper) { border-radius: 100px; padding-left: 16px; }
.write-btn { border-radius: 100px; padding: 0 24px; font-weight: 600; box-shadow: var(--shadow-md); transition: transform var(--transition-fast), box-shadow var(--transition-fast); }
.write-btn:hover { transform: translateY(-1px); box-shadow: var(--shadow-lg); }

.action-icons { display: flex; align-items: center; gap: 16px; }
.action-icon { cursor: pointer; color: var(--text-secondary); transition: color var(--transition-fast), transform var(--transition-fast); }
.action-icon:hover { color: var(--juejin-blue); transform: scale(1.1); }
.header-avatar { cursor: pointer; border: 2px solid transparent; transition: border-color var(--transition-fast); }
.header-avatar:hover { border-color: var(--juejin-blue); }
</style>
