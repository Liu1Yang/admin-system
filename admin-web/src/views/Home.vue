<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCurrentUser, logout } from '../api/auth'
import { clearAuth, getUser } from '../utils/auth'

const router = useRouter()
const loggingOut = ref(false)

const user = computed(() => getUser())

async function testMe() {
  try {
    const res = await getCurrentUser()
    ElMessage.success(`/me 成功：${res.data.username}，权限 ${res.data.permissions.length} 个`)
  } catch (e) {
    // handled
  }
}

async function handleLogout() {
  loggingOut.value = true
  try {
    await logout()
    ElMessage.success('已登出，Token 已加入黑名单')
  } catch (e) {
    ElMessage.warning('登出接口失败，已清除本地登录态')
  } finally {
    clearAuth()
    loggingOut.value = false
    router.push('/login')
  }
}
</script>

<template>
  <div class="home-page">
    <el-card class="home-card">
      <h2>Day24 · 路由守卫 + 登出</h2>
      <template v-if="user">
        <p>当前用户：<strong>{{ user.username }}</strong></p>
        <p>角色：{{ user.roles?.map((r) => r.name).join('、') || '无' }}</p>
        <p class="hint">未登录访问首页会自动跳转 /login；Token 失效时接口 401 也会跳回登录页。</p>
        <el-space wrap>
          <el-button type="primary" @click="testMe">测试 GET /api/auth/me</el-button>
          <el-button type="danger" :loading="loggingOut" @click="handleLogout">登出</el-button>
        </el-space>
      </template>
    </el-card>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.home-card {
  width: 560px;
}

.hint {
  color: #909399;
  font-size: 14px;
  line-height: 1.6;
}
</style>
