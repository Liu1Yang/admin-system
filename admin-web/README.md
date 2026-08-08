# admin-web

Vue3 + Vite + Element Plus 管理端，对接 `admin-system` 后端。

## 技术栈

- Vue 3 + Vite
- Element Plus + Icons
- Axios + Vue Router

## 启动

```bash
# 1. 先启动后端（8080）+ MySQL + Redis
cd ../admin-system
mvn spring-boot:run

# 2. 安装依赖并启动前端
cd ../admin-web
npm install
npm run dev
```

浏览器访问：http://localhost:5173

## 环境变量

`.env.development` 默认 **留空** `VITE_API_BASE_URL`，请求走 Vite 代理（`/api`、`/uploads` → `8080`）。

## 已实现页面（Day23–Day30）

| 路由 | 页面 | 后端接口 |
|------|------|----------|
| `/login` | 登录 | POST `/api/auth/login` |
| `/` | 首页 Dashboard | — |
| `/users` | 用户列表 | GET/DELETE `/api/users` |
| `/roles` | 角色管理 | GET/POST `/api/roles` |
| `/users` 绑角色 | 弹窗 | GET/POST `/api/users/{id}/roles` |
| `/categories` | 分类树 | `/api/categories/*` |
| `/products` | 商品列表 | GET `/api/products` |
| `/products/create` | 新增商品 | POST `/api/products` |
| `/products/:id/edit` | 编辑商品 | GET/PUT `/api/products/{id}` |

## Day31 联调收尾

完整演示脚本、自检清单、简历描述见：**[../docs/phase-b-demo.md](../docs/phase-b-demo.md)**

按脚本跑通全部勾选 = 阶段 B 前端联调完成，可开始录屏。

## 目录

```text
src/
├── api/          auth、user、role、category、product、file
├── config/       menu.js（菜单 ↔ permission）
├── layout/       AdminLayout.vue
├── router/       路由 + 登录/权限守卫
├── utils/        auth、category、routerHolder
├── views/
│   ├── Login.vue
│   ├── Dashboard.vue
│   ├── user/UserList.vue
│   ├── role/RoleList.vue
│   ├── category/CategoryTree.vue
│   └── product/ProductList.vue、ProductForm.vue
├── App.vue
└── main.js
```
