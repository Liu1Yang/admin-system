# LiuYang 学习仓库

Spring Boot + Vue3 前后端分离后台管理系统。

| 目录 | 说明 |
|------|------|
| [admin-system/](admin-system/) | 后端 API（Spring Boot 2.7 + MyBatis-Plus + MySQL + Redis + JWT） |
| [admin-web/](admin-web/) | 管理端前端（Vue3 + Vite + Element Plus） |
| [docs/phase-b-demo.md](docs/phase-b-demo.md) | Day31 联调演示脚本 + 录屏提纲 + 简历描述 |
| [docs/docker.md](docs/docker.md) | Day32 Docker Compose 部署指南 |
| [docs/prod.md](docs/prod.md) | Day33 生产环境配置与 jar 启动 |
| [docs/deploy.md](docs/deploy.md) | Day34 打包与一键部署 |
| [docs/testing.md](docs/testing.md) | Day35 单元测试 |
| [docs/dual-token.md](docs/dual-token.md) | Day36 双 Token |
| [docs/rate-limit.md](docs/rate-limit.md) | Day37 接口限流（Lua + ZSET） |
| [docs/review.md](docs/review.md) | Day38 总复习 + 简历 + 面试 |
| [docs/mq.md](docs/mq.md) | **阶段 C MQ 学习路线** |
| [docs/mq-day39.md](docs/mq-day39.md) | Day39 RabbitMQ Hello |
| [docs/mq-day40.md](docs/mq-day40.md) | **Day40 ACK / 重试 / 死信** |
| [docs/mq-day41.md](docs/mq-day41.md) | **Day41 异步操作日志** |
| [docs/mq-day42.md](docs/mq-day42.md) | **Day42 操作日志前端** |
| [docs/mq-day43.md](docs/mq-day43.md) | **Day43 DLQ + 告警** |
| [docs/mq-day44.md](docs/mq-day44.md) | **Day44 商品缓存异步刷新** |
| [docs/phase-d.md](docs/phase-d.md) | **阶段 D 工程化与部署进阶** |
| [docs/ci-day45.md](docs/ci-day45.md) | **Day45 GitHub Actions CI** |
| [docs/ci-day46.md](docs/ci-day46.md) | **Day46 前端 Docker + Nginx** |
| [docs/ci-day47.md](docs/ci-day47.md) | **Day47 Nginx 统一入口** |
| [docs/ci-day48.md](docs/ci-day48.md) | **Day48 Actuator 健康检查** |
| [docs/install-wsl.md](docs/install-wsl.md) | **WSL2 安装指南（Docker 前置）** |

## 快速启动

### 方式一：Docker 一键启动（Day32 / Day34）

```powershell
# 在 study 根目录 — 全量构建（镜像内 Maven）
docker compose up -d --build

# 或：本地先打 jar，再构建镜像（更快，Day34）
.\scripts\up.ps1 -UseLocalJar
```

详见 [docs/docker.md](docs/docker.md)、[docs/deploy.md](docs/deploy.md)。后端：http://localhost:8080

### 方式二：本地开发

```bash
# 1. MySQL + Redis，按 admin-system/sql/init-phase-a-order.md 执行 SQL

# 2. 后端
cd admin-system
mvn spring-boot:run

# 3. 前端
cd admin-web
npm install
npm run dev
```

- 前端：http://localhost:5173
- 接口文档：http://localhost:8080/doc.html
- 测试账号：admin / liuyang，密码 **123456**

## 学习进度

### 阶段 A（Day1–Day20）✅ 后端

RBAC、商品模块、Redis 缓存、Token 黑名单、Postman 联调

### 阶段 B1–B2（Day23–Day31）✅ 前端 + 联调

- [x] Day23：CORS + Vue 项目骨架
- [x] Day24：登录 + Token + 路由守卫
- [x] Day25：Layout + 权限菜单
- [x] Day26：用户列表（分页、搜索）
- [x] Day27：角色管理 + 用户绑角色
- [x] Day28：分类树 CRUD
- [x] Day29：商品列表（多条件搜索）
- [x] Day30：商品表单（新增/编辑、封面、上下架）
- [x] Day31：联调收尾 + 演示脚本（见 docs/phase-b-demo.md）

### 阶段 B3 部署 ✅

- [x] Day32：Docker Compose（MySQL + Redis + 应用，见 docs/docker.md）
- [x] Day33：`application-prod.yml` + 启动文档（见 docs/prod.md）
- [x] Day34：jar 打包 + Docker 一键启动（见 docs/deploy.md）

### 阶段 B3+ ✅

- [x] Day35：单元测试（JWT / Auth / 黑名单 / Health，见 docs/testing.md）
- [x] Day36：双 Token Access + Refresh（见 docs/dual-token.md）
- [x] Day37：接口限流 Lua+ZSET（见 docs/rate-limit.md）
- [x] Day38：收尾与复习（见 [docs/review.md](docs/review.md)）

### 阶段 C MQ ✅

- [x] Day39：RabbitMQ 概念 + Docker + 生产者/消费者（见 [docs/mq-day39.md](docs/mq-day39.md)）
- [x] Day40：ACK、重试、死信队列（见 [docs/mq-day40.md](docs/mq-day40.md)）
- [x] Day41：接入业务 — 异步操作日志（见 [docs/mq-day41.md](docs/mq-day41.md)）
- [x] Day42：操作日志前端页面（见 [docs/mq-day42.md](docs/mq-day42.md)）
- [x] Day43：操作日志 DLQ + 告警（见 [docs/mq-day43.md](docs/mq-day43.md)）
- [x] Day44：商品变更异步刷新 Redis（见 [docs/mq-day44.md](docs/mq-day44.md)）

### 阶段 D 工程化（进行中）

- [x] Day45：GitHub Actions CI（见 [docs/ci-day45.md](docs/ci-day45.md)）
- [x] Day46：前端 Docker 化（Nginx，见 [docs/ci-day46.md](docs/ci-day46.md)）
- [x] Day47：Nginx 统一入口（见 [docs/ci-day47.md](docs/ci-day47.md)）
- [x] Day48：Actuator 健康检查（见 [docs/ci-day48.md](docs/ci-day48.md)）
- [ ] Day49：阶段 D 总复习

详见 [docs/phase-d.md](docs/phase-d.md)。

## IDEA 打开方式

打开 **`D:\project\study`** 作为项目根目录（不是 admin-system 子目录）。
