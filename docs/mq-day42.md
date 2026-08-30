# Day42 操作日志前端页面

> **目标：** 管理端查看 Day41 异步写入的操作日志，权限 `log:read`。

---

## 一、新增文件

| 文件 | 说明 |
|------|------|
| `admin-web/src/api/operationLog.js` | 调用 `GET /api/operation-logs` |
| `admin-web/src/views/operationLog/OperationLogList.vue` | 分页列表 + 模块/操作人筛选 |

## 二、路由与菜单

**router/index.js**

```javascript
{
  path: 'operation-logs',
  name: 'OperationLogs',
  component: OperationLogList,
  meta: { title: '操作日志', permission: 'log:read' }
}
```

**config/menu.js**

```javascript
{ path: '/operation-logs', title: '操作日志', icon: 'Document', permission: 'log:read' }
```

- 侧边栏：`hasPermission('log:read')` 才显示
- 路由守卫：无权限访问时跳转首页

---

## 三、验收步骤

**1. 确保已执行 `operation-log.sql`（Day41）**

**2. 启动前后端**

```powershell
# 后端
cd D:\project\study\admin-system
mvn spring-boot:run

# 前端
cd D:\project\study\admin-web
npm run dev
```

**3. admin 登录** → 侧边栏应出现「操作日志」

**4. 先做几次写操作**（新增分类、修改商品等）

**5. 打开「操作日志」页** → 应看到记录，含操作人、模块、动作、URI、耗时等

**6. liuyang 登录** → 侧边栏无「操作日志」；手动访问 `/operation-logs` 会跳回首页

---

## Day42 验收

- [ ] admin 能看到菜单和列表
- [ ] 筛选模块/操作人有效
- [ ] liuyang 看不到菜单
- [ ] 列表数据与后端 `[MQ 操作日志 ACK]` 对应

---

## 下一步

[Day43 操作日志 DLQ + 告警](./mq-day43.md)
