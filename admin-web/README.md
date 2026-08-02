# admin-web

Vue3 + Vite + Element Plus 管理端，对接 `admin-system` 后端。

## 技术栈

- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router

## 启动

```bash
# 1. 先启动后端（8080）+ MySQL + Redis
cd ../admin-system
mvn spring-boot:run

# 2. 安装依赖并启动前端
cd ../admin-web
npm install
npm run dev
```

浏览器访问：http://localhost:5173

## 环境变量

`.env.development` 默认 **留空** `VITE_API_BASE_URL`，请求走 Vite 代理（`/api` → `8080`），开发时无 CORS 问题。

## Day26 验收

1. `admin / 123456` 登录 → 进入「用户管理」
2. 表格展示用户列表（ID、用户名、昵称、创建时间）
3. 输入用户名关键字 → 点击「查询」→ 模糊搜索生效
4. 切换页码 / 每页条数 → 分页正常
5. 点击「删除」→ 确认后调用 `DELETE /api/users/{id}`，列表刷新

## 目录

```text
src/
├── api/          Axios 封装、接口（auth.js、user.js）
├── config/       菜单配置（permission 映射）
├── layout/       AdminLayout 布局
├── router/       路由 + 守卫
├── utils/        auth、routerHolder
├── views/        页面（user/UserList.vue 等）
├── App.vue
└── main.js
```
