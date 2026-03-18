# Echo Community 任务进度清单

> 📅 创建时间：2026-03-17 | 项目路径：`D:\AtoC\dev\soft_projects\echo-community`

## 一、评论功能移除

### 后端清理（Codex 负责）
- [x] 删除 CommentController / CommentService / CommentMapper / Comment Entity
  > ✅ 完成时间：2026-03-17 13:50 | 遇到问题：无
- [x] 删除 CommentListResponse / CreateCommentRequest DTO
- [x] 删除 CommentMapper.xml
- [x] init.sql 删除 comment 表建表语句
- [x] 确认删除后 `mvn compile` 无报错

### 前端清理（Gemini 负责）
- [x] Home.vue 删除评论数图标（ChatDotSquare + commentCount）
  > ✅ 完成时间：2026-03-17 14:00
- [x] PostDetail.vue 侧栏删除「💬 评论」统计行 + comment-notice CSS
- [x] 删除 api/comment.ts
- [x] 清理 Profile.vue / Notifications.vue 中评论引用

## 二、种子数据优化（Codex 负责）
- [x] seed.sql 从 600+ 条缩减到 15 条
  > ✅ 完成时间：2026-03-17 13:50
- [x] 每条 content 扩展到 300-800 字真实技术内容
- [x] comment_count 全部设为 0
- [x] 重新导入数据库验证

## 三、专栏模块

### 后端（Codex 负责）
- [x] init.sql 新增 `column_article` 表
- [x] ColumnArticle Entity / Mapper / XML / Service / Controller
- [x] 🖱️ API 实测：✅ GET /api/columns 返回 9 条数据

### 前端（Gemini 负责）
- [x] api/column.ts API 调用
- [x] column/index.vue 专栏卡片列表页
- [x] 🖱️ UI 交互验证：✅ 路由 /column HTTP 200

### 种子数据（Codex 负责）
- [x] 插入 9 条专栏文章（3 个专栏分组）

## 四、面经模块

### 后端（Codex 负责）
- [x] init.sql 新增 `interview` 表
- [x] Interview Entity / Mapper / Service / Controller / XML
- [x] 🖱️ API 实测：✅ GET /api/interviews 返回 9 条数据

### 前端（Gemini 负责）
- [x] api/interview.ts API 调用
- [x] interview/index.vue 面经列表页（公司/岗位/难度/结果）
- [x] 🖱️ UI 交互验证：✅ 路由 /interview HTTP 200

### 种子数据（Codex 负责）
- [x] 插入 9 条面经数据

## 五、沸点模块

### 后端（Codex 负责）
- [x] init.sql 新增 `moment` 表
- [x] Moment Entity / Mapper / Service / Controller / XML
- [x] 🖱️ API 实测：✅ GET /api/moments 返回 12 条数据

### 前端（Gemini 负责）
- [x] api/moment.ts API 调用
- [x] moment/index.vue 沸点 Timeline 动态页
- [x] 🖱️ UI 交互验证：✅ 路由 /moment HTTP 200

### 种子数据（Codex 负责）
- [x] 插入 12 条沸点动态

## 六、最终 Review（Claude 负责）
- [ ] Codex 后端代码审查 → 评分：__ | 结果：
- [ ] Gemini 前端代码审查 → 评分：__ | 结果：
- [ ] 质量门禁验证 → 测试通过率：__ | Lint：__ | 构建：__
- [ ] 🖱️ UI 全量交互验证 → 结果：
- [ ] 集成测试验证 → 结果：
- [ ] 安全检查 → 结果：

## 跨界修改记录（锁日志）
| 状态 | 模型 | 目标文件 | 原因 | 变更摘要 | 自检 |
|------|------|---------|------|---------|------|
