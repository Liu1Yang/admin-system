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

## Day28 验收

1. `admin` 登录 → 「分类管理」展示树形结构（数码 → 手机/电脑，服装等）
2. 选中「数码」→「新增子分类」→ 创建成功并出现在树下
3. 选中节点 →「编辑」→ 修改名称/排序 → 保存生效
4. 删除有子节点的分类 → 后端拒绝并提示；删除叶子节点 → 成功

## 目录

```text
src/
├── api/          Axios 封装、接口（auth.js、user.js）
├── config/       菜单配置（permission 映射）
├── layout/       AdminLayout 布局
├── router/       路由 + 守卫
├── utils/        auth、routerHolder
├── views/        页面（user/、role/、category/ 等）
├── App.vue
└── main.js
```
