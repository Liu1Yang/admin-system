# 阶段 D：工程化与部署进阶

> 阶段 C（Day39～44 MQ）完成后开始。目标：让项目具备**自动化构建、前后端统一部署、可观测性**。

## 学习顺序

| Day | 主题 | 文档 | 状态 |
|-----|------|------|------|
| **Day45** | GitHub Actions CI | [ci-day45.md](./ci-day45.md) | ✅ |
| Day46 | 前端 Docker 化（Nginx 托管 dist） | ci-day46.md（待写） | ⏳ |
| Day47 | Nginx 反向代理（统一入口） | ci-day47.md（待写） | ⏳ |
| Day48 | Actuator 健康检查增强 | ci-day48.md（待写） | ⏳ |
| Day49 | 阶段 D 总复习 + 全链路演示 | ci-day49.md（待写） | ⏳ |

## 和阶段 B3 部署的关系

| | Day32～34 | 阶段 D |
|--|-----------|--------|
| 范围 | 本地 Docker Compose 一键启 | **CI 自动验证** + 生产级部署结构 |
| 前端 | `npm run dev` 开发 | **打包进镜像**，Nginx 托管 |
| 入口 | 前端 5173 + 后端 8080 | **Nginx 统一 80 端口** |
| 质量 | 手动 `mvn test` | **Push 自动跑测试** |

## 项目内新增文件（Day45）

```text
.github/workflows/ci.yml   # Push/PR 自动跑后端测试 + 前端构建
docs/ci-day45.md
```

## 下一步

完成 [Day45 验收](./ci-day45.md) 后，说「继续 Day46」做前端 Docker 化。
