<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteUser, getUserPage } from '../../api/user'
import { hasPermission } from '../../utils/auth'

const loading = ref(false)
const tableData = ref([])

const query = reactive({
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

const canDelete = hasPermission('user:delete')

async function fetchList() {
  loading.value = true
  try {
    const res = await getUserPage({
      page: query.page,
      size: query.size,
      username: query.username || undefined
    })
    const page = res.data
    tableData.value = page.records || []
    pagination.total = page.total || 0
    pagination.current = page.current || query.page
    pagination.size = page.size || query.size
    pagination.pages = page.pages || 0
  } catch (e) {
    // handled
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  fetchList()
}

function handleReset() {
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

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除用户「${row.username}」吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && query.page > 1) {
      query.page -= 1
    }
    fetchList()
  } catch (e) {
    // cancel or error
  }
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
      <el-form-item label="用户名">
        <el-input
          v-model="query.username"
          placeholder="模糊搜索"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" min-width="120" />
      <el-table-column prop="nickname" label="昵称" min-width="120" />
      <el-table-column label="创建时间" min-width="180">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column v-if="canDelete" label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
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
