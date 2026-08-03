# admin-web

Vue3 + Vite + Element Plus 管理端，对接 `admin-system` 后端。

## 技术栈

- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router

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

`.env.development` 默认 **留空** `VITE_API_BASE_URL`，请求走 Vite 代理（`/api` → `8080`），开发时无 CORS 问题。

## Day29 验收

1. `admin` 登录 → 「商品管理」展示商品列表（iPhone、MacBook、T 恤等）
2. 名称搜索 `iPhone` → 只显示匹配商品
3. 分类选「手机」、状态选「上架」→ 筛选生效
4. 填写价格区间 → 查询结果在范围内
5. 点击「下架/上架」→ 状态切换；有 `product:delete` 时可删除

## 目录

```text
src/
├── api/          Axios 封装、接口（auth、user、role、category、product）
├── config/       菜单配置（permission 映射）
├── layout/       AdminLayout 布局
├── router/       路由 + 守卫
├── utils/        auth、routerHolder
├── views/        页面（user/、role/、category/、product/ 等）
├── App.vue
└── main.js
```
