# Day44 商品变更异步刷新 Redis 缓存

> **目标：** 商品写操作不再同步写 Redis，改为发 MQ，由 Consumer 异步刷新/删除缓存。

---

## 一、为什么异步刷缓存？

| 同步（改前） | MQ 异步（Day44） |
|-------------|-----------------|
| 写接口 RT 含 Redis 耗时 | 写接口只发消息，更快返回 |
| 多实例各自刷缓存易不一致 | Consumer 统一处理，可水平扩展 |
| 与 DB 事务耦合 | 解耦，最终一致 |

**读路径不变：** `getById` 仍先查 Redis，未命中再查 DB 并回填。

---

## 二、链路

```text
ProductServiceImpl.create/update/delete
        ↓
ProductCacheProducer.sendRefresh / sendDelete
        ↓
admin.product.cache.exchange → admin.product.cache.queue
        ↓
ProductCacheConsumer
        ↓
REFRESH: SELECT DB → productCacheService.set
DELETE: productCacheService.delete
```

---

## 三、Producer 调用点

| 操作 | 消息 |
|------|------|
| create | REFRESH（Consumer 从 DB 加载后写入） |
| update / updateStatus / updateCover | REFRESH |
| delete | DELETE |

---

## 四、验收步骤

**1. 确保 RabbitMQ 运行，重启后端**

**2. admin 登录，修改某个商品**（如改名称）

**3. 观察 app 日志：**

```text
[MQ 商品缓存] REFRESH productId=1 name=新名称
```

**4. 再次 GET 商品详情** — 应读到最新数据（可能极短暂延迟，通常毫秒级）

**5. 删除商品后：**

```text
[MQ 商品缓存] DELETE productId=1
```

**6. RabbitMQ 管理界面** — `admin.product.cache.queue` 消费后为 0

---

## 五、 eventual consistency 说明

修改商品后 **极短时间内**（MQ 消费前）若立刻 `getById`，可能仍读到旧缓存。  
生产环境常见做法：

- 写后短 TTL + 异步刷新（本项目）
- 或写时先 `delete` 再异步 `set`（更强一致）

---

## 六、面试怎么说

> 写路径发 MQ 通知缓存更新，读路径不变；Consumer 按 action 刷新或删除 Redis key，实现写读解耦和最终一致；多实例部署时各实例 Consumer 都会收到消息，保证缓存同步。

---

## Day44 验收

- [ ] 修改商品后日志有 `[MQ 商品缓存] REFRESH`
- [ ] 删除商品后日志有 `[MQ 商品缓存] DELETE`
- [ ] 商品详情能读到最新数据
- [ ] 能解释为何用 MQ 刷缓存

---

## 阶段 C 延伸完成

Day42～44 在 Day41 基础上完成：**前端展示 → DLQ 告警 → 缓存异步刷新**。
