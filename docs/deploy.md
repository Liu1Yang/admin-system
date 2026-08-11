# Day34 打包与一键部署

> **目标：** 本地打出 jar → Docker 一键拉起全栈 → 验收通过。  
> 前置：Docker Desktop 已安装（见 [install-wsl.md](./install-wsl.md)）。

## 两种 Docker 构建方式

| 方式 | 何时用 | 命令 |
|------|--------|------|
| **A. 全量构建** | 不改代码、直接演示 | `docker compose up -d --build` |
| **B. 本地 jar** | 已 `mvn package`，加快镜像构建 | 见下方「方式 B」 |

Dockerfile 内 **Maven 编译** vs **只 COPY jar** 是 CI/CD 里常见优化：开发机/流水线先打包，服务器只构建运行时镜像。

---

## 方式 A：一条命令（Day32 延续）

```powershell
cd D:\project\study
docker compose up -d --build
```

Docker 会在镜像里执行 `mvn package`，首次较慢。

---

## 方式 B：本地 jar + Docker（Day34 重点）

### 1. 打包

```powershell
cd D:\project\study\admin-system
mvn clean package -DskipTests
```

确认存在：`target/admin-system-1.0.0.jar`

### 2. 一键启动（脚本）

```powershell
cd D:\project\study
.\scripts\up.ps1 -UseLocalJar
```

或手动：

```powershell
cd D:\project\study
docker compose -f docker-compose.yml -f docker-compose.jar.yml up -d --build
```

`Dockerfile.jar` 只复制 jar，**不跑 Maven**，重建 app 镜像通常几十秒。

### 3. （可选）环境变量

```powershell
copy .env.example .env
# 编辑 .env 修改 MYSQL_PASSWORD、JWT_SECRET
docker compose up -d --build
```

Compose 会自动读取根目录 `.env`（已在 `.gitignore`，勿提交）。

---

## 验收清单

```powershell
# 容器状态
docker compose ps

# 健康检查
Invoke-RestMethod http://localhost:8080/api/health

# 登录
Invoke-RestMethod -Method Post http://localhost:8080/api/auth/login `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"123456"}'
```

| 检查项 | 期望 |
|--------|------|
| mysql / redis / app | 均为 running，app 依赖 healthy |
| `/api/health` | `code: 200`, `status: UP` |
| 登录 | 返回 `token` |
| 前端 | `cd admin-web && npm run dev`，5173 可登录 |

---

## 部署路径总览

```text
开发机                          服务器 / 演示机
────────                        ────────────────
mvn package          ──jar──►    java -jar ... --spring.profiles.active=prod
     │                                    （见 prod.md）
     │
     └──jar──► Dockerfile.jar ──► docker compose up -d
                                    （本 Day34）
```

| Profile | 启动方式 | 文档 |
|---------|----------|------|
| `dev` | `mvn spring-boot:run` | README |
| `docker` | `docker compose up` | [docker.md](./docker.md) |
| `prod` | `java -jar` + 环境变量 | [prod.md](./prod.md) |

---

## 常用命令

```powershell
docker compose logs -f app      # 看后端日志
docker compose restart app      # 只重启应用
docker compose down             # 停止（保留数据）
docker compose down -v          # 停止并清空数据库
```

---

## 故障排查

| 现象 | 处理 |
|------|------|
| `Dockerfile.jar` 找不到 jar | 先 `mvn clean package -DskipTests` |
| 8080 被占用 | `docker compose stop app` 或改端口映射 |
| 拉镜像超时 | [docker.md](./docker.md) 国内镜像加速 |
| 中文乱码 | [docker.md](./docker.md) 中文乱码修复 |

---

## Day34 验收

- [ ] `mvn clean package -DskipTests` 成功，jar 存在
- [ ] `.\scripts\up.ps1 -UseLocalJar` 或 compose jar 模式启动成功
- [ ] 三个容器 running，`/api/health` 200
- [ ] admin 登录拿到 token
- [ ] 理解「全量 Dockerfile」与「Dockerfile.jar」区别
