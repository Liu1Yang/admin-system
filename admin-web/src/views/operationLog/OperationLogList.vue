<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getOperationLogPage } from '../../api/operationLog'

const loading = ref(false)
const tableData = ref([])

const query = reactive({
  module: '',
  username: '',
  page: 1,
  size: 10
})

const pagination = reactive({
  total: 0,
  current: 1,
  size: 10,
  pages: 0
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getOperationLogPage({
      page: query.page,
      size: query.size,
      module: query.module || undefined,
      username: query.username || undefined
    })
    const page = res.data
    tableData.value = page.records || []
    pagination.total = page.total || 0
    pagination.current = page.current || query.page
    pagination.size = page.size || query.size
    pagination.pages = page.pages || 0
  } catch (e) {
    // handled by request interceptor
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  fetchList()
}

function handleReset() {
  query.module = ''
  query.username = ''
  query.page = 1
  fetchList()
}

function handlePageChange(page) {
  query.page = page
  fetchList()
}

function handleSizeChange(size) {
  query.size = size
  query.page = 1
  fetchList()
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

onMounted(fetchList)
</script>

<template>
  <el-card shadow="never">
    <el-form :inline="true" @submit.prevent="handleSearch">
      <el-form-item label="模块">
        <el-input
          v-model="query.module"
          placeholder="如 商品"
          clearable
          style="width: 160px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="操作人">
        <el-input
          v-model="query.username"
          placeholder="用户名"
          clearable
          style="width: 160px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="操作人" width="100" />
      <el-table-column prop="module" label="模块" width="90" />
      <el-table-column prop="action" label="动作" width="90" />
      <el-table-column prop="method" label="方法" width="80" />
      <el-table-column prop="uri" label="URI" min-width="180" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP" width="130" />
      <el-table-column label="结果" width="80">
        <template #default="{ row }">
          <el-tag :type="row.success ? 'success' : 'danger'" size="small">
            {{ row.success ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="durationMs" label="耗时(ms)" width="90" />
      <el-table-column label="时间" min-width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </el-card>
</template>

<style scoped>
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
