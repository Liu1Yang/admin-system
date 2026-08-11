# Day33 生产环境配置与启动指南

> **prod**  profile 用于在 **服务器 / 虚拟机** 上直接跑 jar（不依赖 Docker Compose）。  
> 本地开发仍用 **dev**；Docker 演示仍用 **docker**（见 [docker.md](./docker.md)）。

## 三种 Profile 对比

| Profile | 场景 | 配置来源 | 接口文档 |
|---------|------|----------|----------|
| `dev` | 本地开发 | `application-dev.yml`，写死 localhost | ✅ 开启 |
| `docker` | Docker Compose | `application-docker.yml`，服务名 mysql/redis | ✅ 开启 |
| `prod` | 生产部署 | `application-prod.yml`，**环境变量** | ❌ 关闭 |

---

## 前置条件

1. 服务器已安装 **JDK 8+**
2. **MySQL 8**、**Redis** 已部署（可与应用同机或独立）
3. 数据库已按 `admin-system/sql/init-phase-a-order.md` 顺序执行脚本
4. 防火墙放行 `8080`（或你自定义的 `SERVER_PORT`）

---

## 1. 打包

```bash
cd admin-system
mvn clean package -DskipTests
```

产物：`admin-system/target/admin-system-1.0.0.jar`

---

## 2. 配置环境变量

**生产环境必须设置**（无默认值，缺了启动会报错）：

| 变量 | 说明 | 示例 |
|------|------|------|
| `MYSQL_PASSWORD` | MySQL 密码 | `your-db-password` |
| `JWT_SECRET` | JWT 签名密钥（≥32 字符） | `change-me-to-a-long-random-string-32+` |

**可选**（有默认值）：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `SERVER_PORT` | `8080` | 应用端口 |
| `MYSQL_HOST` | `127.0.0.1` | MySQL 地址 |
| `MYSQL_PORT` | `3306` | MySQL 端口 |
| `MYSQL_DATABASE` | `admin_system` | 库名 |
| `MYSQL_USER` | `root` | 数据库用户 |
| `REDIS_HOST` | `127.0.0.1` | Redis 地址 |
| `REDIS_PORT` | `6379` | Redis 端口 |
| `REDIS_PASSWORD` | 空 | Redis 密码 |
| `JWT_EXPIRATION` | `86400000` | Token 有效期（毫秒） |
| `FILE_UPLOAD_DIR` | `/data/admin-system/uploads` | 上传目录（Linux） |
| `LOG_FILE` | `logs/admin-system.log` | 日志文件路径 |

### Windows PowerShell 示例

```powershell
cd D:\project\study\admin-system

$env:MYSQL_HOST = "127.0.0.1"
$env:MYSQL_PASSWORD = "123456"
$env:REDIS_HOST = "127.0.0.1"
$env:JWT_SECRET = "liuyang-admin-system-jwt-secret-key-2024-study-demo"
$env:FILE_UPLOAD_DIR = "D:\data\admin-system\uploads"

New-Item -ItemType Directory -Force -Path $env:FILE_UPLOAD_DIR
New-Item -ItemType Directory -Force -Path logs

java -jar target/admin-system-1.0.0.jar --spring.profiles.active=prod
```

### Linux 示例

```bash
export MYSQL_HOST=127.0.0.1
export MYSQL_PASSWORD='your-db-password'
export REDIS_HOST=127.0.0.1
export JWT_SECRET='your-long-random-secret-at-least-32-chars'
export FILE_UPLOAD_DIR=/data/admin-system/uploads

mkdir -p "$FILE_UPLOAD_DIR" logs

nohup java -jar admin-system-1.0.0.jar --spring.profiles.active=prod > logs/console.log 2>&1 &
```

---

## 3. 验证

```bash
curl http://localhost:8080/api/health
```

期望：`"status":"UP"`

生产环境 **不应** 访问 `/doc.html`（已关闭 Knife4j）。

---

## 4. 与 Docker 的关系

| 方式 | 启动命令 | Profile |
|------|----------|---------|
| Docker Compose | `docker compose up -d` | `docker`（Dockerfile 内写死） |
| 服务器直跑 jar | `java -jar ... --spring.profiles.active=prod` | `prod` |

Day34 会把 **jar + Docker** 再整合成「构建一次、到处运行」的完整流程 → 见 [deploy.md](./deploy.md)。

## 生产安全 Checklist

- [ ] `JWT_SECRET` 使用随机长字符串，**不要**用 dev 默认值
- [ ] `MYSQL_PASSWORD` 使用强密码
- [ ] Redis 设密码并限制内网访问
- [ ] 上传目录、日志目录权限最小化
- [ ] 前置 Nginx 做 HTTPS 与反向代理
- [ ] 定期备份 MySQL 与 `uploads` 目录

---

## 常见问题

| 现象 | 处理 |
|------|------|
| 启动报 `Could not resolve placeholder 'JWT_SECRET'` | 未设置环境变量，见上文表格 |
| 连不上 MySQL | 检查 `MYSQL_HOST`、防火墙、用户权限 |
| 上传图片 404 | 确认 `FILE_UPLOAD_DIR` 存在且进程有写权限 |
| 想临时开文档调试 | 勿在生产开；用 dev profile 或 docker 本地排查 |

---

## Day33 验收

- [ ] `application-prod.yml` 存在，敏感项走环境变量
- [ ] `mvn clean package -DskipTests` 成功
- [ ] 设置环境变量后 `--spring.profiles.active=prod` 能启动
- [ ] `GET /api/health` 返回 200
- [ ] `/doc.html` 在生产 profile 下不可访问（404 或禁用）
