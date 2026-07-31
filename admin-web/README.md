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

## Day25 验收

1. `admin / 123456` 登录 → 进入 Layout，侧边栏显示：**首页、用户、角色、分类、商品**
2. 退出后用 `liuyang / 123456` 登录 → 侧边栏**仅显示首页**（无 RBAC 写权限）
3. liuyang 手动访问 `/users` → 被路由守卫重定向回 `/`
4. 顶栏显示昵称/角色，点击「退出」正常登出

## 目录

```text
src/
├── api/          Axios 封装、接口
├── config/       菜单配置（permission 映射）
├── layout/       AdminLayout 布局
├── router/       路由 + 守卫
├── utils/        auth、routerHolder
├── views/        页面
├── App.vue
└── main.js
```
