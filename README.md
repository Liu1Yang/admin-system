# admin-system（Monorepo）

前后端分离后台管理系统，用于学习与求职作品展示。

| 目录 | 说明 |
|------|------|
| [admin-system/](admin-system/) | 后端 API（Spring Boot 2.7 + MyBatis-Plus + MySQL + Redis + JWT） |
| [admin-web/](admin-web/) | 管理端前端（Vue3 + Vite + Element Plus） |

## 快速启动

### 后端

```bash
cd admin-system
# 按 admin-system/sql/init-phase-a-order.md 顺序执行 SQL
mvn spring-boot:run
```

文档：http://localhost:8080/doc.html

### 前端

```bash
cd admin-web
npm install
npm run dev
```

访问：http://localhost:5173

## 环境要求

- JDK 8+、Maven 3.6+
- MySQL 5.7+ / 8.0
- Redis 5.0+
- Node.js 18+（前端）

详细说明见各子目录 README。
