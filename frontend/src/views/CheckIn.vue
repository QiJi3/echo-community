<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/useUserStore'

// Mock: 本月签到数据 (日期 -> 是否签到)
const today = new Date()
const year = today.getFullYear()
const month = today.getMonth()

const signedDays = ref<Set<number>>(new Set([1, 2, 3, 5, 6, 7, 8, 10, 11, 12]))

const userStore = useUserStore()
const isTodaySigned = computed(() => userStore.hasCheckedInToday || signedDays.value.has(today.getDate()))

const daysInMonth = computed(() => new Date(year, month + 1, 0).getDate())

const monthName = computed(() => `${year} 年 ${month + 1} 月`)

const firstDayOfWeek = computed(() => new Date(year, month, 1).getDay())

const consecutiveDays = computed(() => {
  let count = 0
  for (let d = today.getDate(); d >= 1; d--) {
    if (signedDays.value.has(d)) count++
    else break
  }
  return count
})

const handleCheckin = () => {
  if (isTodaySigned.value) {
    ElMessage.warning('今天已经签到过了 ✅')
    return
  }
  signedDays.value.add(today.getDate())
  userStore.hasCheckedInToday = true
  ElMessage.success(`签到成功！连续签到 ${consecutiveDays.value} 天 🎉`)
}

const weekdays = ['日', '一', '二', '三', '四', '五', '六']
</script>

<template>
  <div class="main-container">
    <div class="content-area">
      <div class="echo-panel checkin-panel">
        <div class="checkin-header">
          <h2>每日签到</h2>
          <p class="checkin-desc">坚持签到，获取经验值和社区徽章</p>
        </div>

        <div class="checkin-stats">
          <div class="stat-card">
            <div class="stat-number">{{ signedDays.size }}</div>
            <div class="stat-label">本月签到</div>
          </div>
          <div class="stat-card highlight">
            <div class="stat-number">{{ consecutiveDays }}</div>
            <div class="stat-label">连续签到</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ signedDays.size * 10 }}</div>
            <div class="stat-label">获得经验</div>
          </div>
        </div>

        <el-button 
          :type="isTodaySigned ? 'info' : 'primary'" 
          size="large" 
          class="checkin-btn"
          :disabled="isTodaySigned"
          @click="handleCheckin"
        >
          {{ isTodaySigned ? '✅ 今日已签到' : '🔥 立即签到' }}
        </el-button>

        <!-- Calendar -->
        <div class="calendar-section">
          <div class="calendar-title">{{ monthName }}</div>
          <div class="calendar-grid">
            <div v-for="w in weekdays" :key="w" class="calendar-weekday">{{ w }}</div>
            <div v-for="_ in firstDayOfWeek" :key="'empty-'+_" class="calendar-day empty"></div>
            <div 
              v-for="d in daysInMonth" 
              :key="d"
              class="calendar-day"
              :class="{ 
                signed: signedDays.has(d), 
                today: d === today.getDate(),
                future: d > today.getDate()
              }"
            >
              <span class="day-num">{{ d }}</span>
              <el-icon v-if="signedDays.has(d)" class="day-icon"><Check /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>

    <aside class="sidebar-area">
      <div class="echo-panel sidebar-card">
        <div class="title-row"><h4>签到规则</h4></div>
        <ul class="rule-list">
          <li>每日签到获得 <strong>10 经验值</strong></li>
          <li>连续签到 7 天额外奖励 <strong>50 经验值</strong></li>
          <li>连续签到 30 天获得 <strong>「坚持不懈」徽章</strong></li>
          <li>中断签到后连续天数重新计算</li>
        </ul>
      </div>
      <div class="echo-panel sidebar-card">
        <div class="title-row"><h4>签到排行榜</h4></div>
        <div class="rank-item" v-for="(u, i) in ['RedisGuru', '前端大牛', 'Architect007']" :key="u">
          <span class="rank-num" :class="'rank-' + (i+1)">{{ i + 1 }}</span>
          <span class="rank-name">{{ u }}</span>
          <span class="rank-days">{{ 28 - i * 3 }} 天</span>
        </div>
      </div>
    </aside>
  </div>
</template>

<style scoped>
.checkin-panel {
  padding: 32px;
}

.checkin-header h2 {
  margin: 0 0 8px 0;
  font-size: 22px;
  color: var(--text-primary);
}

.checkin-desc {
  color: var(--text-tertiary);
  font-size: 14px;
  margin: 0 0 24px 0;
}

.checkin-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  flex: 1;
  background: var(--bg-color);
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}

.stat-card.highlight {
  background: linear-gradient(135deg, #1e80ff 0%, #4e9fff 100%);
  color: #fff;
}

.stat-card.highlight .stat-label {
  color: rgba(255,255,255,0.8);
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-tertiary);
}

.checkin-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  margin-bottom: 32px;
}

/* Calendar */
.calendar-section {
  border-top: 1px solid var(--border-color);
  padding-top: 24px;
}

.calendar-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  text-align: center;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-weekday {
  text-align: center;
  font-size: 13px;
  color: var(--text-tertiary);
  padding: 8px 0;
  font-weight: 500;
}

.calendar-day {
  text-align: center;
  padding: 8px 0;
  border-radius: 6px;
  position: relative;
  font-size: 14px;
  color: var(--text-secondary);
}

.calendar-day.empty {
  visibility: hidden;
}

.calendar-day.signed {
  background: #eaf2ff;
  color: var(--juejin-blue);
  font-weight: 500;
}

.calendar-day.today {
  border: 2px solid var(--juejin-blue);
  font-weight: 700;
}

.calendar-day.future {
  color: var(--text-tertiary);
  opacity: 0.5;
}

.day-icon {
  position: absolute;
  bottom: 2px;
  right: 50%;
  transform: translateX(50%);
  font-size: 10px;
  color: var(--juejin-blue);
}

/* Sidebar */
.title-row {
  margin-bottom: 12px;
}

.sidebar-card {
  padding: 16px;
}

.sidebar-card h4 {
  margin: 0;
  font-size: 15px;
  color: var(--text-primary);
  font-weight: 600;
}

.rule-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.rule-list li {
  font-size: 14px;
  color: var(--text-secondary);
  padding: 6px 0;
  border-bottom: 1px solid var(--border-color);
}

.rule-list li:last-child {
  border-bottom: none;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-color);
  font-size: 14px;
}

.rank-item:last-child {
  border-bottom: none;
}

.rank-num {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--bg-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  margin-right: 12px;
  color: var(--text-tertiary);
}

.rank-1 { background: #ffd700; color: #fff; }
.rank-2 { background: #c0c0c0; color: #fff; }
.rank-3 { background: #cd7f32; color: #fff; }

.rank-name {
  flex: 1;
  color: var(--text-primary);
}

.rank-days {
  color: var(--juejin-blue);
  font-weight: 500;
}
</style>
