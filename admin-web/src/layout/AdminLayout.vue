<script setup>
import { computed, ref } from 'vue' // Vue 3 的核心，用来定义响应式数据和计算属性。
import { useRoute, useRouter } from 'vue-router' // Vue Router 的 Hook。useRoute 拿到当前页面信息，useRouter 拿到控制跳转的对象。
import { ElMessage } from 'element-plus'  // Element Plus 提供的全局消息提示（比如“已登出”、“登录失败”）。
import { menuItems } from '../config/menu' // 菜单配置文件（存放每个菜单的名称、路径、图标）
import { logout } from '../api/auth' // 后端登出接口
import { clearAuth, getUser, hasPermission } from '../utils/auth' // 本地登录态工具

const route = useRoute() // 代表当前页面的路由对象（用来获取当前路径、页面标题等）
const router = useRouter()  // 代表路由控制器（用来执行跳转，如 router.push('/login')）
const loggingOut = ref(false) // 控制“退出”按钮的加载状态（防连点）
const collapsed = ref(false) // 控制左侧菜单栏是“展开”还是“折叠”

const user = computed(() => getUser())
// 从工具类获取当前登录用户信息。因为是 computed，只要 store 里的用户信息一变，这个 user 变量自动更新。

const visibleMenus = computed(() =>
  menuItems.filter((item) => !item.permission || hasPermission(item.permission))
)
// 【权限控制核心】筛选当前用户可以看见的菜单。
// 逻辑：如果菜单没有要求权限（!item.permission） 或者 用户有权限（hasPermission），就保留这个菜单。

const activeMenu = computed(() => route.path)
// 当前的路由路径。比如你正在 /system/user 页面，这个变量就是 "/system/user"。
// 用来让左侧菜单树高亮显示你当前在哪一页。

async function handleLogout() {
  loggingOut.value = true  // 1. 按钮变转圈（disable，防止你狂点）
  try {
    await logout()   // 2. 调用后端退出接口（告诉服务器我要下线了）
    ElMessage.success('已登出')  // 3. 如果成功，弹窗提示
  } catch (e) {
    ElMessage.warning('登出接口失败，已清除本地登录态') // 4. 就算后端报错/断网，也要保证你能退出
  } finally {
    clearAuth()   // 5. 不管上面有没有报错，强制清除浏览器里的 Token
    loggingOut.value = false
    router.push('/login')
  }
}
</script>

<template> <!---- UI 布局层  <-->
  <el-container class="layout-root">
    <el-aside :width="collapsed ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <span v-if="!collapsed">admin-system</span>
        <span v-else>A</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item v-for="item in visibleMenus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-button link @click="collapsed = !collapsed">
            <el-icon><Expand v-if="collapsed" /><Fold v-else /></el-icon>
          </el-button>
          <span class="page-title">{{ route.meta.title || '首页' }}</span>
        </div>
        <div class="header-right">
          <span class="username">{{ user?.nickname || user?.username }}</span>
          <el-tag v-for="role in user?.roles || []" :key="role.code" size="small" type="info">
            {{ role.name }}
          </el-tag>
          <el-button type="danger" link :loading="loggingOut" @click="handleLogout">退出</el-button>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>   /* 样式层*/
.layout-root {
  min-height: 100vh;
}

.layout-aside {
  background: #304156;
  transition: width 0.2s;
}

.logo {
  height: 56px;
  line-height: 56px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  background: #263445;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #ebeef5;
  background: #fff;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.username {
  color: #606266;
  font-size: 14px;
}

.layout-main {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}
</style>
