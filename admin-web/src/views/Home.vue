<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCurrentUser } from '../api/auth'

const router = useRouter()

const user = computed(() => {
  const raw = localStorage.getItem('user')
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
})

function goLogin() {
  router.push('/login')
}

function logoutLocal() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('已清除本地登录态（Day24 接后端 logout）')
  router.push('/login')
}

async function testMe() {
  try {
    const res = await getCurrentUser()
    ElMessage.success(`/me 成功：${res.data.username}，权限 ${res.data.permissions.length} 个`)
  } catch (e) {
    // handled
  }
}
</script>

<template>
  <div class="home-page">
    <el-card class="home-card">
      <h2>Vue 前端骨架已就绪</h2>
      <template v-if="user">
        <p>当前用户：<strong>{{ user.username }}</strong></p>
        <p>角色：{{ user.roles?.map(r => r.name).join('、') || '无' }}</p>
        <el-space>
          <el-button type="primary" @click="testMe">测试 GET /api/auth/me（CORS）</el-button>
          <el-button @click="logoutLocal">退出（本地）</el-button>
        </el-space>
      </template>
      <template v-else>
        <p>尚未登录</p>
        <el-button type="primary" @click="goLogin">去登录</el-button>
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
  width: 520px;
}
</style>
