# Day32 Docker Compose 部署指南

一键启动 **MySQL + Redis + Spring Boot 后端**。

## 前置要求

- 已安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)（Windows/Mac）
- 端口未被占用：`3306`、`6379`、`8080`

> 若本机已在跑 MySQL/Redis，请先停止，或修改 `docker-compose.yml` 中的端口映射。

## 启动

在仓库根目录 `study/` 执行：

```bash
docker compose up -d --build
```

首次启动会：

1. 拉取 MySQL 8、Redis 7 镜像
2. Maven 构建后端 jar 并打包进 app 镜像
3. 按顺序执行 `admin-system/sql/` 初始化脚本

查看日志：

```bash
docker compose logs -f app
```

看到 `Started AdminApplication` 即启动成功。

## 验证

```bash
# 健康检查
curl http://localhost:8080/api/health

# 登录（PowerShell 可用 Invoke-RestMethod）
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"123456\"}"
```

浏览器：

| 地址 | 说明 |
|------|------|
| http://localhost:8080/doc.html | Knife4j 文档 |
| http://localhost:8080/api/health | 健康检查 |

测试账号：**admin / liuyang**，密码 **123456**

## 前端联调

后端在 Docker 跑起来后，前端仍本地启动：

```bash
cd admin-web
npm run dev
```

访问 http://localhost:5173 ，Vite 代理 `/api` → `8080`。

## 常用命令

```bash
# 停止
docker compose down

# 停止并删除数据卷（会清空数据库，慎用）
docker compose down -v

# 仅重建 app
docker compose up -d --build app

# 查看容器状态
docker compose ps
```

## 目录说明

```text
study/
├── docker-compose.yml      # 编排 MySQL + Redis + app
├── admin-system/
│   ├── Dockerfile          # 多阶段构建 jar
│   └── src/main/resources/
│       └── application-docker.yml   # Docker 环境配置
└── docs/docker.md            # 本文档
```

## 环境变量（app 容器）

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `MYSQL_HOST` | mysql | MySQL 服务名 |
| `MYSQL_USER` | root | 数据库用户 |
| `MYSQL_PASSWORD` | 123456 | 数据库密码 |
| `REDIS_HOST` | redis | Redis 服务名 |

## Docker 是干什么的？还要手动开 MySQL/Redis 吗？

**用 Docker 跑 Day32 时，不需要再手动启动本机 MySQL、Redis 和后端 jar。**

一条命令：

```bash
docker compose up -d --build
```

会按 `docker-compose.yml` 自动完成：

| 容器 | 作用 |
|------|------|
| `admin-mysql` | 数据库 + 首次启动执行 `sql/` 初始化脚本 |
| `admin-redis` | 缓存 / Token 黑名单等 |
| `admin-app` | Spring Boot 后端（Maven 构建后打包进镜像） |

容器之间用服务名 `mysql`、`redis` 互联，无需改 hosts。

**停止全部环境：**

```bash
docker compose down
```

**对比本地开发（Day31 及以前）：**

| 方式 | 你要做的 |
|------|----------|
| 本地开发 | 自己开 MySQL、Redis，`mvn spring-boot:run` |
| Docker（Day32） | 只开 Docker Desktop，再 `docker compose up`；前端仍 `npm run dev` |

Docker 的价值：**环境一致、一键启停、不污染本机**；换电脑只要装 Docker 就能复现同样环境。

---

## 国内镜像加速（拉镜像超时必看）

国内直连 `registry-1.docker.io`（Docker Hub）经常超时，报错类似：

```text
failed to resolve reference ... registry-1.docker.io ... connectex ... failed to respond
```

### 方法一：Docker Desktop 配置镜像加速（推荐，一劳永逸）

1. 打开 **Docker Desktop → Settings → Docker Engine**
2. 在 JSON 中加入 `registry-mirrors`（保留原有其它字段）：

```json
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://docker.1ms.run"
  ]
}
```

3. 点击 **Apply & Restart**
4. 重新执行 `docker compose up -d --build`

### 方法二：临时手动拉取并打标签

镜像加速未生效时，可先走 DaoCloud 再 `docker tag`：

```powershell
docker pull docker.m.daocloud.io/library/mysql:8.0
docker pull docker.m.daocloud.io/library/redis:7-alpine
docker pull docker.m.daocloud.io/library/maven:3.9-eclipse-temurin-8
docker pull docker.m.daocloud.io/library/eclipse-temurin:8-jre-jammy

docker tag docker.m.daocloud.io/library/mysql:8.0 mysql:8.0
docker tag docker.m.daocloud.io/library/redis:7-alpine redis:7-alpine
docker tag docker.m.daocloud.io/library/maven:3.9-eclipse-temurin-8 maven:3.9-eclipse-temurin-8
docker tag docker.m.daocloud.io/library/eclipse-temurin:8-jre-jammy eclipse-temurin:8-jre-jammy
```

然后再 `docker compose up -d --build`。

> 镜像源可能变动；若某个地址失效，可搜索「Docker 镜像加速 2025」换最新可用源。

---

## 故障排查

| 现象 | 处理 |
|------|------|
| 拉镜像超时 / `registry-1.docker.io` 连不上 | 见上文 **国内镜像加速** |
| 昵称/角色/分类乱码 | 见下方 **中文乱码修复** |

### 中文乱码修复

**原因：** Docker 首次初始化 SQL 时编码不对；若用 PowerShell 管道执行含中文的 SQL，还可能变成 `???`。

**修复当前数据（不删库）：**

```powershell
docker cp D:\project\study\admin-system\sql\fix-charset-data.sql admin-mysql:/tmp/fix-charset-data.sql
docker exec admin-mysql mysql -uroot -p123456 --default-character-set=utf8mb4 admin_system -e "source /tmp/fix-charset-data.sql"
```

然后 **退出登录 → 重新登录**（JWT 里可能还缓存旧昵称）。

**全新初始化（会清空 Docker 数据库）：**

```powershell
docker compose down -v
docker compose up -d
```

已在各 `sql/*.sql` 开头加入 `SET NAMES utf8mb4`，新装不会再乱码。
| app 启动失败连不上 MySQL | `docker compose logs mysql` 看初始化是否完成；等 mysql healthy 后再看 app |
| 3306 端口冲突 | 改 compose 为 `"3307:3306"`，本机连 3307 |
| 改 SQL 后数据没更新 | `docker compose down -v` 清卷后重新 up |
| 上传图片丢失 | 图片在 `app_uploads` 卷，down 不带 `-v` 会保留 |

## Day32 验收

- [ ] `docker compose up -d --build` 三个容器均为 running
- [ ] `GET /api/health` 返回 200
- [ ] admin 登录返回 token
- [ ] 前端 `npm run dev` 能正常登录并访问商品列表
