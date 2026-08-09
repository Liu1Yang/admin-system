# LiuYang 学习仓库

Spring Boot + Vue3 前后端分离后台管理系统。

| 目录 | 说明 |
|------|------|
| [admin-system/](admin-system/) | 后端 API（Spring Boot 2.7 + MyBatis-Plus + MySQL + Redis + JWT） |
| [admin-web/](admin-web/) | 管理端前端（Vue3 + Vite + Element Plus） |
| [docs/phase-b-demo.md](docs/phase-b-demo.md) | Day31 联调演示脚本 + 录屏提纲 + 简历描述 |
| [docs/docker.md](docs/docker.md) | Day32 Docker Compose 部署指南 |
| [docs/prod.md](docs/prod.md) | **Day33 生产环境配置与 jar 启动** |
| [docs/install-wsl.md](docs/install-wsl.md) | **WSL2 安装指南（Docker 前置）** |

## 快速启动

### 方式一：Docker（Day32，推荐演示/部署）

```bash
# 在 study 根目录
docker compose up -d --build
```

详见 [docs/docker.md](docs/docker.md)。后端：http://localhost:8080

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

### 阶段 B3 部署（进行中）

- [x] Day32：Docker Compose（MySQL + Redis + 应用，见 docs/docker.md）
- [x] Day33：`application-prod.yml` + 启动文档（见 docs/prod.md）
- [ ] Day34：jar 打包 + Docker 一键启动验证

### 阶段 B3+（未开始）
- [ ] Day35–38：单元测试、双 Token、限流等

## IDEA 打开方式

打开 **`D:\project\study`** 作为项目根目录（不是 admin-system 子目录）。
