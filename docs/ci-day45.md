# Day45 GitHub Actions CI

> **目标：** Push 代码后自动跑后端单元测试、打包 jar，并验证前端能成功 `npm run build`。

---

## 一、为什么需要 CI？

| 手动 | CI（持续集成） |
|------|----------------|
| 改完代码忘了跑 `mvn test` | 每次 Push 自动跑 |
| 本地能跑、别人拉下来报错 | 统一在干净环境验证 |
| 简历写「熟悉 CI/CD」没项目支撑 | 仓库有 `.github/workflows/ci.yml` |

**面试点：** CI 在合并前发现问题；CD 是自动部署（Day46～47 会涉及部署结构）。

---

## 二、本项目的 CI 做了什么

文件：`.github/workflows/ci.yml`

```text
Push / PR → main 或 master
    ├── job: backend
    │     JDK 8 → mvn test → mvn package
    └── job: frontend
          Node 20 → npm ci → npm run build
```

两个 job **并行**执行，互不影响。

---

## 三、本地先验证（和 CI 一样）

**后端：**

```powershell
cd D:\project\study\admin-system
mvn test
mvn -DskipTests package
```

**前端：**

```powershell
cd D:\project\study\admin-web
npm ci
npm run build
```

`dist/` 目录生成即构建成功。

---

## 四、推送到 GitHub 触发 CI

**1. 确保代码已提交**

```powershell
cd D:\project\study
git add .
git commit -m "feat: Day45 GitHub Actions CI"
```

**2. 推送到 GitHub**（需已配置 remote）

```powershell
git push origin main
```

**3. 打开 GitHub 仓库 → Actions 标签页**

应看到 **CI** workflow 运行，两个 job 都绿 ✅。

> 没有 GitHub 远程也没关系：本地 `mvn test` + `npm run build` 通过即算验收。

---

## 五、workflow 关键配置说明

| 配置 | 含义 |
|------|------|
| `on: push / pull_request` | 推代码或开 PR 时触发 |
| `actions/setup-java` + `cache: maven` | 自动缓存 Maven 依赖，加速构建 |
| `actions/setup-node` + `cache: npm` | 缓存 node_modules |
| `npm ci` | 按 lock 文件精确安装（CI 推荐，比 `npm install` 稳定） |
| `mvn -B` | Batch 模式，CI 日志更干净 |

---

## 六、常见问题

### Q: CI 红了但本地能跑？

- 看 Actions 日志具体哪一步失败
- 常见：没提交 `package-lock.json`、JDK 版本不一致

### Q: 测试需要 MySQL/Redis 吗？

不需要。Day35 写的单元测试全部 **Mock**，不启 Spring 容器、不连真实中间件。

### Q: 和 Docker 部署冲突吗？

不冲突。CI 只验证「能编译、能测」；Docker 是运行时部署（Day32～34、Day46～47）。

---

## 七、面试怎么说

> 项目配置了 GitHub Actions：后端 JDK8 跑 `mvn test` 和打包，前端 Node 跑 `npm ci` + `build`，Push 到 main 自动触发，保证合并前代码可构建、测试通过。

---

## Day45 验收

- [ ] 本地 `mvn test` 通过
- [ ] 本地 `npm run build` 通过
- [ ] 仓库有 `.github/workflows/ci.yml`
- [ ] （可选）GitHub Actions 页面两个 job 全绿
- [ ] 能解释 CI 和手动测试的区别

---

## 下一步 Day46

前端 Docker 化：把 `dist/` 打进 Nginx 镜像，不再依赖 `npm run dev`。

完成后说「继续 Day46」。
