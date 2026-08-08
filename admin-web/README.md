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

## Day30 验收

1. 商品列表点「新增商品」→ 填写表单 → 创建成功（默认下架）
2. 新增时上传封面 → 预览显示；保存后列表可见封面
3. 点「编辑」→ 修改名称/价格/库存 → 保存生效
4. 编辑页点「上架」→ 库存 > 0 时成功；库存为 0 时后端拒绝
5. 编辑页「更换封面」→ 调用 `POST /api/products/{id}/cover` 一步绑定

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
