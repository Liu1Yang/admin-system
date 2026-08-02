<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteUser, getUserPage, getUserRoles, assignUserRoles } from '../../api/user'
import { getRoleList } from '../../api/role'
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
const canAssignRole = hasPermission('role:assign')

const roleDialogVisible = ref(false)
const roleSubmitting = ref(false)
const allRoles = ref([])
const selectedRoleIds = ref([])
const currentUser = ref(null)

async function fetchList() { //  列表查询（稳定输出） 从后端拉取表格数据并更新到前端的状态中。
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

async function openRoleDialog(row) {  // 并行获取数据 点击“分配角色”按钮时，同时拉取“所有可选角色”和“当前用户已有的角色”。
  currentUser.value = row
  roleDialogVisible.value = true
  try {
    const [rolesRes, userRolesRes] = await Promise.all([
      getRoleList(),
      getUserRoles(row.id)
    ])
    allRoles.value = rolesRes.data || []
    selectedRoleIds.value = (userRolesRes.data || []).map((r) => r.id)
  } catch (e) {
    roleDialogVisible.value = false
  }
}

async function handleAssignRoles() {
  if (!currentUser.value) return
  roleSubmitting.value = true
  try {
    await assignUserRoles(currentUser.value.id, selectedRoleIds.value)
    ElMessage.success('角色绑定成功')
    roleDialogVisible.value = false
  } catch (e) {
    // handled
  } finally {
    roleSubmitting.value = false
  }
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

const showActions = canDelete || canAssignRole

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
      <el-table-column v-if="showActions" label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button v-if="canAssignRole" type="primary" link @click="openRoleDialog(row)">
            绑角色
          </el-button>
          <el-button v-if="canDelete" type="danger" link @click="handleDelete(row)">
            删除
          </el-button>
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

    <el-dialog
      v-model="roleDialogVisible"
      :title="`绑定角色 - ${currentUser?.username || ''}`"
      width="460px"
      destroy-on-close
    >
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="role in allRoles" :key="role.id" :value="role.id">
          {{ role.name }}（{{ role.code }}）
        </el-checkbox>
      </el-checkbox-group>
      <p v-if="!allRoles.length" class="empty-tip">暂无角色，请先在角色管理页创建</p>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleSubmitting" @click="handleAssignRoles">
          保存
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.el-checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.empty-tip {
  color: #909399;
  font-size: 14px;
}
</style>
