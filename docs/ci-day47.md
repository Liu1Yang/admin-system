# Day47 Nginx 反向代理统一入口

> **目标：** 用户只访问 **一个端口（80）**，前端、API、接口文档全部经 Nginx 转发；后端不对外暴露 8080。

---

## 一、Day46 vs Day47

| | Day46 | Day47 |
|--|-------|-------|
| 前端 | Nginx 托管 dist | 同左 |
| API | `/api` 反代 | 同左 + **Swagger/ Knife4j** |
| 后端 8080 | 仍映射到宿主机 | **默认不映射**（仅 Docker 内网） |
| 文档 | 要访问 `:8080/doc.html` | **http://localhost/doc.html** |

---

## 二、部署拓扑

```text
浏览器
   │
   ▼ :80
┌─────────────┐
│  admin-web  │  Nginx
│  (nginx)    │
└──────┬──────┘
       │ Docker 内网 app:8080
       ▼
┌─────────────┐
│  admin-app  │  Spring Boot（不暴露宿主机端口）
└─────────────┘
```

**Nginx 转发规则（nginx.conf）：**

| 路径 | 转发到 |
|------|--------|
| `/` | Vue 静态 `dist/` |
| `/api/` | `app:8080` |
| `/uploads/` | `app:8080` |
| `/doc.html`、`/swagger-ui/`、`/v3/api-docs/` 等 | `app:8080` |

---

## 三、Compose 文件说明

| 文件 | 用途 |
|------|------|
| `docker-compose.yml` | 默认全栈；`app` 仅 `expose: 8080`（内网） |
| `docker-compose.prod.yml` | 显式生产拓扑（`ports: !reset []`） |
| `docker-compose.dev-api.yml` | **调试**时额外映射 `8080:8080` |

---

## 四、验收步骤

**1. 重建并启动**

```powershell
cd D:\project\study
docker compose up -d --build
```

**2. 确认 app 没有 8080 映射**

```powershell
docker compose ps
```

`admin-app` 的 PORTS 列应**没有** `0.0.0.0:8080`，只有 `8080/tcp`（内部）。

**3. 统一入口验证**

| 地址 | 预期 |
|------|------|
| http://localhost | 登录页 |
| http://localhost/api/health | `status: UP` |
| http://localhost/doc.html | Knife4j 接口文档 |

**4. 登录 admin / 123456**，商品列表、操作日志正常。

**5. （可选）需要直连后端调试**

```powershell
docker compose -f docker-compose.yml -f docker-compose.dev-api.yml up -d
```

此时 `http://localhost:8080/api/health` 也可用。

---

## 五、一键脚本

```powershell
# 生产拓扑（与默认相同，显式 prod overlay）
.\scripts\up.ps1 -Prod

# 本地 jar + 生产拓扑
.\scripts\up.ps1 -UseLocalJar -Prod
```

---

## 六、面试怎么说

> 生产环境只暴露 Nginx 80 端口，Spring Boot 仅在容器网络内监听；Nginx 按路径转发静态资源、REST API 和 Swagger 文档，减少攻击面，也避免前端跨域。

---

## Day47 验收

- [ ] `admin-app` 无宿主机 8080 映射
- [ ] http://localhost 可登录使用
- [ ] http://localhost/doc.html 可打开文档
- [ ] http://localhost/api/health 返回 UP
- [ ] 能画出「浏览器 → Nginx → app」拓扑

---

## 下一步 Day48

Spring Boot **Actuator** 健康检查增强（就绪探针、依赖状态）。

完成后说「继续 Day48」。
