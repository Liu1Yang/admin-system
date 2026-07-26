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

若需直连后端测 CORS，改为：

```text
VITE_API_BASE_URL=http://localhost:8080
```

并确保后端已重启（`CorsConfig` + 拦截器放行 OPTIONS）。

## Day23 验收

1. 打开 http://localhost:5173/login
2. 使用 admin / 123456 登录 → 跳转首页
3. 点击「测试 GET /api/auth/me」→ 提示成功（说明 **CORS + Token** 正常）

## 目录

```text
src/
├── api/          Axios 封装、接口
├── router/       路由
├── views/        页面
├── App.vue
└── main.js
```
