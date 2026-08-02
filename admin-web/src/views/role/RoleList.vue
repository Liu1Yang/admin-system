<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createRole, getRoleList } from '../../api/role'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const submitting = ref(false)

const form = reactive({
  code: '',
  name: ''
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getRoleList()
    tableData.value = res.data || []
  } catch (e) {
    // handled
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  form.code = ''
  form.name = ''
  dialogVisible.value = true
}

async function handleCreate() {
  if (!form.code || !form.name) {
    ElMessage.warning('请填写角色编码和名称')
    return
  }
  submitting.value = true
  try {
    await createRole({ code: form.code, name: form.name })
    ElMessage.success('角色创建成功')
    dialogVisible.value = false
    fetchList()
  } catch (e) {
    // handled
  } finally {
    submitting.value = false
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
    <div class="toolbar">
      <el-button type="primary" @click="openCreateDialog">新增角色</el-button>
    </div>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="code" label="角色编码" min-width="120" />
      <el-table-column prop="name" label="角色名称" min-width="120" />
      <el-table-column label="创建时间" min-width="180">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增角色" width="420px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="编码">
          <el-input v-model="form.code" placeholder="如 EDITOR（大写）" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="如 编辑" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
</style>
