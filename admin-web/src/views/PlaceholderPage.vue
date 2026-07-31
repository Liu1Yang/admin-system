<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <el-card shadow="never">
    <h2>{{ route.meta.title }}</h2>
    <p class="hint">该页面将在后续 Day 中对接后端 API 实现。</p>
  </el-card>
</template>

<style scoped>
.hint {
  color: #909399;
}
</style>
