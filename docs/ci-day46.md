# Day46 前端 Docker 化（Nginx 托管 dist）

> **目标：** 前端不再依赖 `npm run dev`，打包进 Docker 镜像，由 Nginx 提供静态页面。

---

## 一、和开发模式的区别

| | `npm run dev`（Day23～） | Docker + Nginx（Day46） |
|--|--------------------------|-------------------------|
| 端口 | 5173 | **80** |
| 静态资源 | Vite 实时编译 | 预构建 `dist/` |
| API | Vite proxy → 8080 | **Nginx 反代** → app:8080 |
| 用途 | 本地开发 | **演示 / 部署** |

---

## 二、镜像结构

```text
admin-web/Dockerfile（多阶段）
  Stage 1: node:20 → npm ci → npm run build → dist/
  Stage 2: nginx:alpine → 复制 dist + nginx.conf
```

**nginx.conf 做了两件事：**

1. `try_files` — Vue SPA 路由回退 `index.html`
2. `/api/`、`/uploads/` — 反向代理到 `app:8080`（Docker 内网）

因此 `.env.production` 里 `VITE_API_BASE_URL` **留空**，浏览器走同源，无需 CORS。

---

## 三、验收步骤

**1. 确保 Docker Desktop 已启动**

**2. 构建并启动全栈（含 web）**

```powershell
cd D:\project\study
docker compose up -d --build
```

首次构建 `web` 会跑 `npm ci` + `vite build`，约 1～3 分钟。

**3. 查看容器**

```powershell
docker compose ps
```

应有 `admin-web` 状态 Up，端口 `0.0.0.0:80->80`。

**4. 浏览器打开**

http://localhost

- 应看到登录页（不是 404）
- 用 **admin / 123456** 登录
- 能进入用户管理、商品管理等页面

**5. 验证 API 反代**

登录成功即说明 Nginx → `app:8080` 代理正常。

---

## 四、只重建前端

```powershell
cd D:\project\study
docker compose up -d --build web
```

---

## 五、本地开发不受影响

日常改代码仍用：

```powershell
cd admin-web
npm run dev
```

Docker 前端是**部署形态**，不替代 dev server。

---

## 六、面试怎么说

> 前端多阶段 Docker 镜像：Node 构建 dist，Nginx Alpine 托管静态资源；Nginx 同时反代 `/api` 到 Spring Boot，实现同源部署，生产环境无需暴露 5173 开发端口。

---

## Day46 验收

- [ ] `docker compose ps` 有 `admin-web`
- [ ] http://localhost 能打开登录页并登录
- [ ] 列表页、操作日志等页面正常
- [ ] 能解释多阶段 Dockerfile 的作用

---

## 下一步 Day47

在 Nginx 层做**统一入口**优化（与 Day46 的 web 服务衔接，完善生产部署拓扑）。

完成后说「继续 Day47」。
