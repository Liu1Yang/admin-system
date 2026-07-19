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
- 商品 CRUD（关联分类、默认下架）
- JWT 无状态鉴权
- Redis 缓存用户信息
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

执行 `sql/init.sql`。若已有旧数据，再执行 `sql/update-password-bcrypt.sql`（测试账号密码仍为 `123456`）。

执行 `sql/rbac.sql` 初始化角色权限表（admin → 管理员，liuyang → 普通用户）。

执行 `sql/category.sql` 初始化商品分类表。

执行 `sql/product.sql` 初始化商品表。

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
| 认证 | POST `/api/auth/login`、`/api/auth/register`、GET `/api/auth/me` |
| 用户 | GET `/api/users`（分页）、CRUD `/api/users/{id}` |
| 角色 | GET `/api/roles`、POST `/api/roles`、GET `/api/roles/{id}` |
| 用户角色 | GET `/api/users/{id}/roles`、POST `/api/users/{id}/roles` |
| 分类 | GET `/api/categories/tree`、CRUD `/api/categories/{id}` |
| 商品 | GET `/api/products/{id}`、POST/PUT/DELETE `/api/products` |
| 文件 | POST `/api/files/upload` |

除登录、注册、健康检查、文档页外，业务接口需在 Header 携带：

```text
Authorization: Bearer {token}
```

## RBAC 联调（Day13）

1. 启动项目，确认已执行 `sql/rbac.sql`
2. Postman → Import → 选择 `postman/RBAC.postman_collection.json`
3. 按文件夹顺序执行：**0-准备** → **1-认证** → **2-越权 403** → **3-管理员 200** → **4-恢复数据**
4. MySQL 验证可执行 `sql/rbac-verify.sql`

> 业务错误时 HTTP 状态码仍为 200，请看响应 JSON 里的 `code` 字段（401/403 等）。

## 简历项目描述（可直接改写）

**项目名称：** 后台管理系统

**技术栈：** Spring Boot、MyBatis-Plus、MySQL、Redis、JWT、Knife4j

**项目描述：**

- 基于 Spring Boot 构建 RESTful 后台管理系统，采用 Controller-Service-Mapper 分层架构
- 使用 JWT 实现无状态登录鉴权，BCrypt 加密存储用户密码
- 使用 MyBatis-Plus 完成用户 CRUD、分页查询与条件搜索
- 封装 Redis 缓存用户热点数据，更新/删除时主动失效缓存
- 统一异常处理与参数校验，集成 Knife4j 自动生成接口文档
- 支持本地图片上传与静态资源访问

## 学习进度

本项目为 LiuYang 的 Spring Boot 学习仓库，按天完成从基础 API 到 JWT、Redis 的企业级功能。
