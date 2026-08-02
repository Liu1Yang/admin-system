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

## Day27 验收

1. `admin` 登录 → 「角色管理」展示 ADMIN、USER 等角色
2. 点击「新增角色」→ 填写编码/名称 → 创建成功
3. 「用户管理」→ 对 liuyang 点「绑角色」→ 勾选 ADMIN → 保存
4. liuyang 重新登录 → 首页权限列表变化，侧边栏菜单增多
5. 可再把 liuyang 改回 USER 角色验证覆盖式绑定

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
