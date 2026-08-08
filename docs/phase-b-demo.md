# 阶段 B 联调演示脚本（Day31）

> 建议录屏 3～5 分钟，按下面顺序操作。测试账号：**admin / 123456**，**liuyang / 123456**。

## 录屏前准备（约 2 分钟，可不录）

```bash
# 1. MySQL + Redis 已启动
# 2. SQL 已按 admin-system/sql/init-phase-a-order.md 顺序执行

# 终端 1：后端
cd admin-system
mvn spring-boot:run

# 终端 2：前端
cd admin-web
npm run dev
```

确认：

- http://localhost:8080/api/health → `code: 200`
- http://localhost:5173/login 可打开

---

## 演示流程（建议录制部分）

### 1. 登录与 Layout（30 秒）

1. 打开 http://localhost:5173/login
2. 使用 **admin / 123456** 登录
3. 展示：**侧边栏**（首页、用户、角色、分类、商品）、**顶栏**（用户名、角色、退出）

**口述要点：** Vue3 管理端 + JWT 登录，菜单按 RBAC 权限动态显隐。

---

### 2. 首页（10 秒）

1. 点击「首页」
2. 展示当前用户角色与 **permissions 列表**

**口述要点：** 登录后后端返回 roles + permissions，前端存 localStorage 控制菜单。

---

### 3. 用户管理（40 秒）

1. 进入「用户管理」
2. 搜索 `liu` → 展示分页列表
3. 对 **liuyang** 点「绑角色」→ 勾选 **USER**（或演示改角色）→ 保存

**口述要点：** 对接 `GET /api/users` 分页搜索；`POST /api/users/{id}/roles` 覆盖式绑角色。

---

### 4. 角色管理（20 秒）

1. 进入「角色管理」
2. 展示 ADMIN / USER 列表（可选：新增一个测试角色）

**口述要点：** `GET /api/roles`，管理员可 `POST /api/roles` 新增。

---

### 5. 分类树（40 秒）

1. 进入「分类管理」
2. 展示树：**数码 → 手机/电脑**、**服装**
3. 选中「数码」→「新增子分类」→ 输入名称保存
4. （可选）删除刚创建的叶子节点

**口述要点：** `GET /api/categories/tree`，Element Plus `el-tree` 展示；有子节点时删除会被后端拒绝。

---

### 6. 商品管理（90 秒）

1. 进入「商品管理」
2. 演示筛选：名称 `iPhone`、分类「手机」、状态「上架」
3. 点「新增商品」→ 填名称/分类/价格/库存 → **上传封面** → 创建
4. 回到列表 → 点「编辑」→ 修改库存 → **上架**
5. （可选）列表里「下架」或「删除」

**口述要点：**

- 列表：`GET /api/products` 多条件分页
- 表单：`POST/PUT /api/products`
- 封面：`POST /api/files/upload` 或 `POST /api/products/{id}/cover`
- 上下架：`PUT /api/products/{id}/status`，库存为 0 不能上架

---

### 7. 登出与权限对比（40 秒）

1. 顶栏点「退出」→ 回到登录页
2. 使用 **liuyang / 123456** 登录
3. 展示：侧边栏 **只有首页**（或权限较少）
4. 手动访问 `/users` → 被路由守卫重定向到首页

**口述要点：** 前端路由守卫 + 菜单 permission；后端接口仍有 403 兜底。

---

### 8. 收尾（10 秒，可选）

1. admin 重新登录
2. 浏览器 F12 → Application → 展示 `token` / `user` 本地存储
3. 或展示 Knife4j：http://localhost:8080/doc.html

---

## 联调自检清单

| # | 项 | 通过 |
|---|-----|------|
| 1 | 未登录访问 `/` → 跳转 `/login` | ☐ |
| 2 | admin 登录后 5 个菜单可见 | ☐ |
| 3 | liuyang 登录后仅首页（或权限符合预期） | ☐ |
| 4 | 用户列表分页 + 搜索 | ☐ |
| 5 | 绑角色后重新登录 permissions 变化 | ☐ |
| 6 | 分类树增删改 | ☐ |
| 7 | 商品列表多条件搜索 | ☐ |
| 8 | 商品新增 + 封面上传 | ☐ |
| 9 | 商品编辑 + 上下架 | ☐ |
| 10 | 退出后 Token 失效（黑名单） | ☐ |

全部打勾 = **阶段 B 前端联调完成**。

---

## 简历项目描述（阶段 A + B，可直接改写）

**项目名称：** 企业级后台管理系统（前后端分离）

**技术栈：** Spring Boot 2.7、MyBatis-Plus、MySQL、Redis、JWT、Vue3、Element Plus、Vite

**项目描述：**

- 后端 RESTful API：RBAC 权限、商品分类树、商品 CRUD、Redis 缓存与 Token 黑名单登出
- 前端 Vue3 管理端：Layout、路由守卫、按 permissions 显隐菜单
- 完成用户/角色/分类/商品全链路页面，对接分页、搜索、表单、文件上传
- 使用 Postman + 浏览器完成阶段 A/B 联调

**个人职责：**

- 独立完成前后端核心模块开发与联调
- 设计数据库表与 RBAC 模型，实现接口级鉴权
- 封装 Axios 统一请求、401 拦截、权限工具函数
