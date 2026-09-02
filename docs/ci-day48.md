# Day48 Actuator 健康检查增强

> **目标：** 引入 Spring Boot Actuator，自动检测 MySQL / Redis / RabbitMQ 状态，支持 K8s 就绪/存活探针。

---

## 一、和 `/api/health` 的区别

| | `/api/health`（Day32 起） | `/actuator/health`（Day48） |
|--|---------------------------|------------------------------|
| 实现 | 手写 Controller | **Spring Boot Actuator** |
| 检测范围 | 固定返回 UP | **自动检测** db、redis、rabbit |
| 用途 | 简单存活、演示 | **运维 / 容器探针** |
| 详情 | 项目名、作者 | 各组件 UP/DOWN |

两者并存：`/api/health` 保持兼容；运维看 Actuator。

---

## 二、自动检测的组件

启用 `spring-boot-starter-actuator` 后，项目已有依赖会自动注册 HealthIndicator：

| 组件 | 条件 |
|------|------|
| **db** | 配置了 `spring.datasource` |
| **redis** | 配置了 `spring.redis` |
| **rabbit** | 配置了 `spring.rabbitmq` |

全部 UP 时整体 `status: UP`；任一 DOWN 则整体 DOWN。

---

## 三、关键配置

**application.yml（公共）：**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      probes:
        enabled: true   # 开启 K8s 探针端点
```

**探针地址：**

| 端点 | 用途 |
|------|------|
| `/actuator/health/liveness` | 存活探针（进程是否活着） |
| `/actuator/health/readiness` | 就绪探针（能否接流量，含依赖检查） |
| `/actuator/health` | 完整健康详情 |
| `/actuator/info` | 应用信息（name、author） |

**环境差异：**

| Profile | `show-details` |
|---------|----------------|
| dev / docker | `always`（学习用，可看组件详情） |
| prod | `never`（只暴露 health，不泄露内部信息） |

---

## 四、验收步骤

**1. 本地启动（MySQL + Redis + RabbitMQ 都要运行）**

```powershell
cd D:\project\study\admin-system
mvn spring-boot:run
```

**2. 完整健康检查**

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

预期（dev 环境）类似：

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "rabbit": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

**3. 就绪 / 存活探针**

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health/readiness
Invoke-RestMethod http://localhost:8080/actuator/health/liveness
```

**4. 应用信息**

```powershell
Invoke-RestMethod http://localhost:8080/actuator/info
```

**5. Docker 统一入口（Day47）**

```powershell
Invoke-RestMethod http://localhost/actuator/health
```

**6. 模拟故障（可选）**

停掉 Redis：`docker stop admin-redis`  
再访问 `/actuator/health` → `redis.status` 应为 **DOWN**，整体可能 DOWN。

---

## 五、Nginx 反代

`admin-web/nginx.conf` 已增加 `/actuator/` 反代，Docker 部署可通过 80 端口访问。

---

## 六、面试怎么说

> 简单存活用 `/api/health`；生产用 Actuator 的 `/actuator/health/readiness` 做就绪探针，自动聚合 MySQL、Redis、MQ 状态；prod 环境 `show-details: never` 避免泄露内部信息。

---

## Day48 验收

- [ ] `/actuator/health` 返回 UP，含 db / redis / rabbit
- [ ] `/actuator/health/liveness` 和 `/readiness` 可访问
- [ ] `/actuator/info` 有 app 信息
- [ ] Docker 下 `http://localhost/actuator/health` 可访问
- [ ] 停 Redis 后 redis 组件变 DOWN（可选）

---

## 下一步 Day49

阶段 D 总复习 + 全链路演示脚本。

完成后说「继续 Day49」。
