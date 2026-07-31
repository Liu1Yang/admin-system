<script setup>
import { computed } from 'vue'
import { getUser } from '../utils/auth'

const user = computed(() => getUser())
</script>

<template>
  <el-card shadow="never">
    <h2 class="title">欢迎回来，{{ user?.nickname || user?.username }}</h2>
    <p class="desc">Day25 · 后台 Layout 已就绪，侧边栏根据 permissions 动态显隐。</p>

    <el-descriptions :column="1" border class="info-block">
      <el-descriptions-item label="用户名">{{ user?.username }}</el-descriptions-item>
      <el-descriptions-item label="角色">
        {{ user?.roles?.map((r) => r.name).join('、') || '无' }}
      </el-descriptions-item>
      <el-descriptions-item label="权限">
        <el-tag
          v-for="perm in user?.permissions || []"
          :key="perm.code"
          size="small"
          class="perm-tag"
        >
          {{ perm.code }}
        </el-tag>
        <span v-if="!user?.permissions?.length">无</span>
      </el-descriptions-item>
    </el-descriptions>

    <el-alert
      type="info"
      :closable="false"
      show-icon
      title="验收提示"
      description="admin 账号应看到全部菜单；liuyang 账号登录后侧边栏仅显示「首页」。"
      class="tip"
    />
  </el-card>
</template>

<style scoped>
.title {
  margin: 0 0 8px;
}

.desc {
  margin: 0 0 20px;
  color: #909399;
}

.info-block {
  max-width: 640px;
}

.perm-tag {
  margin-right: 6px;
  margin-bottom: 4px;
}

.tip {
  margin-top: 20px;
  max-width: 640px;
}
</style>
