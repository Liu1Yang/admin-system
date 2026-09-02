# 阶段 E：进阶选修（Day50+）

> **Day1～49 主线已完结。** 阶段 E 为**选修**，按求职方向和个人兴趣选学，不必全部完成。

## 推荐路线

| Day | 主题 | 适合谁 | 状态 |
|-----|------|--------|------|
| Day50 | Spring Security 替换手写 JWT | 想补安全框架标准方案 | ⏳ |
| Day51 | 接口集成测试（Testcontainers） | 想提升测试含金量 | ⏳ |
| Day52 | HTTPS + 域名部署（Nginx / Caddy） | 想上线真实环境 | ⏳ |
| Day53 | Kubernetes 入门（minikube 部署） | 想冲云原生岗位 | ⏳ |
| Day54 | 阶段 E 总复习 + 作品集打包 | 求职冲刺 | ⏳ |

## 各方向一句话

### Day50 Spring Security

用 `SecurityFilterChain` 替代 `JwtInterceptor`，学习 `@PreAuthorize`、标准 OAuth2 Resource Server 思路。  
**前置：** 熟记当前 JWT + RBAC 实现（Day36～37）。

### Day51 Testcontainers

`@SpringBootTest` + 真实 MySQL/Redis 容器跑集成测试，CI 里自动拉镜像。  
**前置：** Day35 单元测试、Day45 CI。

### Day52 HTTPS

Let's Encrypt + Nginx/Caddy 配置 TLS，HTTP 跳转 HTTPS。  
**前置：** Day47 Nginx 统一入口。

### Day53 Kubernetes

把 `docker-compose` 拆成 Deployment + Service + Ingress；Actuator 探针对接 liveness/readiness。  
**前置：** Day48 Actuator、Day47 部署拓扑。

## 如何开始

在聊天里说你想学的方向，例如：

- 「继续 Day50 Spring Security」
- 「继续 Day51 集成测试」

我会按该 Day 写文档并带你在本项目里落地。

## 当前项目已具备（求职够用）

```text
✅ 前后端分离 CRUD + RBAC
✅ Redis 缓存 / 限流 / 双 Token
✅ RabbitMQ 业务落地
✅ Docker + Nginx + Actuator
✅ GitHub Actions CI
✅ 单元测试 + 完整文档（Day1～49）
```

建议：**先按 [ci-day49.md](./ci-day49.md) 录一版全栈演示视频、更新简历**，再选阶段 E 一个方向深化。
