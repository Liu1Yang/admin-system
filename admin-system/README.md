# admin-system

基于 Spring Boot 的后台管理系统，用于学习与求职作品展示。

## 技术栈

- Spring Boot 2.7
- MyBatis-Plus
- MySQL
- Redis
- JWT + BCrypt
- Knife4j（OpenAPI3）
- Maven

## 功能列表

- 用户注册 / 登录 / 获取当前用户
- 用户 CRUD、分页、模糊搜索
- RBAC 角色查询、用户绑定角色、接口级权限控制（403）
- 登录 / 当前用户接口返回角色与权限列表
- 商品分类树形 CRUD
- 商品 CRUD（关联分类、默认下架）、分页多条件搜索、上下架与库存校验、封面图上传、详情 Redis 缓存
- JWT 无状态鉴权，Redis Token 黑名单实现登出失效
- Redis 缓存用户信息、商品详情
- 统一返回、全局异常、参数校验
- Knife4j 接口文档
- 图片上传（本地存储）

## 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+ / 8.0
- Redis 5.0+

## 快速启动

### 1. 初始化数据库

按顺序执行（清单见 `sql/init-phase-a-order.md`）：

```text
sql/init.sql
sql/update-password-bcrypt.sql   ← 测试账号密码仍为 123456
sql/rbac.sql
sql/category.sql
sql/product.sql
```

### 2. 修改配置

编辑 `src/main/resources/application-dev.yml`：

- MySQL 用户名密码
- Redis 地址（默认 `localhost:6379`）

### 3. 启动项目

```bash
mvn spring-boot:run
```

或在 IDEA 中运行 `AdminApplication`。

### 4. 访问地址

| 地址 | 说明 |
|------|------|
| http://localhost:8080/doc.html | Knife4j 接口文档 |
| http://localhost:8080/api/health | 健康检查 |

## 打包部署

```bash
mvn clean package -DskipTests
java -jar target/admin-system-1.0.0.jar
```

## 接口说明（核心）

| 模块 | 接口 |
|------|------|
| 认证 | POST `/api/auth/login`、`/api/auth/register`、POST `/api/auth/logout`、GET `/api/auth/me` |
| 用户 | GET `/api/users`（分页）、CRUD `/api/users/{id}` |
| 角色 | GET `/api/roles`、POST `/api/roles`、GET `/api/roles/{id}` |
| 用户角色 | GET `/api/users/{id}/roles`、POST `/api/users/{id}/roles` |
| 分类 | GET `/api/categories/tree`、CRUD `/api/categories/{id}` |
| 商品 | GET `/api/products`、GET `/api/products/{id}`、POST `/api/products/{id}/cover`、PUT `/api/products/{id}/status`、POST/PUT/DELETE |
| 文件 | POST `/api/files/upload` |

除登录、注册、健康检查、文档页外，业务接口需在 Header 携带：

```text
Authorization: Bearer {token}
```

## 项目架构（阶段 A）

```text
Client (Postman / 未来 Vue)
        │  Authorization: Bearer {token}
        ▼
┌───────────────────────────────────────────┐
│  Interceptor 链                            │
│  JwtInterceptor → PermissionInterceptor   │
│  (+ Redis Token 黑名单校验)                  │
└───────────────────────────────────────────┘
        ▼
 Controller → Service → Mapper → MySQL
        │              ↘ Redis（用户/商品缓存、Token 黑名单）
        ▼
 Result<T> 统一返回 + GlobalExceptionHandler
```

**核心表：** user、role、permission、user_role、role_permission、category、product

## 阶段 A 联调（Day20）

1. 启动 MySQL + Redis，按上文顺序执行 SQL
2. `mvn spring-boot:run` 或 IDEA 运行 `AdminApplication`
3. Postman 导入：
   - `postman/PhaseA.postman_collection.json` — **全链路联调（推荐）**
   - `postman/RBAC.postman_collection.json` — RBAC 专项（Day13）
4. 按文件夹顺序跑 PhaseA：**1-认证 → 2-分类 → 3-商品 → 4-登出黑名单**
5. 全部 Test 通过 = 阶段 A 后端联调完成

> 业务错误时 HTTP 状态码仍为 200，请看响应 JSON 里的 `code` 字段（401/403/404 等）。

## RBAC 专项联调（Day13）

1. 启动项目，确认已执行 `sql/rbac.sql`
2. Postman → Import → 选择 `postman/RBAC.postman_collection.json`
3. 按文件夹顺序执行：**0-准备** → **1-认证** → **2-越权 403** → **3-管理员 200** → **4-恢复数据**
4. MySQL 验证可执行 `sql/rbac-verify.sql`

> 业务错误时 HTTP 状态码仍为 200，请看响应 JSON 里的 `code` 字段（401/403 等）。

## 简历项目描述（阶段 A 完整版，可直接改写）

**项目名称：** 企业级后台管理系统

**技术栈：** Spring Boot 2.7、MyBatis-Plus、MySQL、Redis、JWT、BCrypt、Knife4j、Maven

**项目描述：**

- 基于 Spring Boot 构建 RESTful 后台管理系统，采用 Controller-Service-Mapper 分层架构
- 设计 RBAC 权限模型（用户-角色-权限），自定义注解 + 拦截器实现接口级鉴权（401/403）
- 使用 JWT 无状态登录，Redis Token 黑名单实现登出失效；BCrypt 加密存储密码
- 实现商品模块：分类树、商品 CRUD、分页多条件搜索、上下架与库存业务校验
- 封装 Redis Cache-Aside 缓存用户/商品热点数据，更新删除时主动失效
- 统一返回体、全局异常、参数校验；集成 Knife4j 接口文档；支持本地图片上传

**个人职责（按实际改写）：**

- 独立完成用户、权限、商品等模块设计与 REST API 开发
- 设计 7 张业务表及关联关系，编写 MyBatis-Plus 数据访问层
- 使用 Postman 完成 RBAC 与商品全链路联调

## 简历项目描述（Day8 基础版，已被上方替代）

**项目名称：** 后台管理系统

**技术栈：** Spring Boot、MyBatis-Plus、MySQL、Redis、JWT、Knife4j

**项目描述：**

- 基于 Spring Boot 构建 RESTful 后台管理系统（早期版本，功能较少）

## 学习进度

本项目为 LiuYang 的 Spring Boot 学习仓库，按天完成从基础 API 到 JWT、Redis 的企业级功能。
