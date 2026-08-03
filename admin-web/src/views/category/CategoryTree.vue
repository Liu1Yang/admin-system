<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createCategory,
  deleteCategory,
  getCategoryTree,
  updateCategory
} from '../../api/category'

const loading = ref(false)
const treeData = ref([])
const currentNode = ref(null)
const dialogVisible = ref(false)
const submitting = ref(false)
const dialogMode = ref('create')

const form = reactive({
  name: '',
  parentId: 0,
  sort: 0
})

const treeProps = {
  label: 'name',
  children: 'children'
}

async function fetchTree() {
  loading.value = true
  try {
    const res = await getCategoryTree()
    treeData.value = res.data || []
  } catch (e) {
    // handled
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.name = ''
  form.parentId = 0
  form.sort = 0
}

function openCreateRoot() {
  dialogMode.value = 'create'
  resetForm()
  form.parentId = 0
  dialogVisible.value = true
}

function openCreateChild() {
  if (!currentNode.value) {
    ElMessage.warning('请先选择父分类')
    return
  }
  dialogMode.value = 'create'
  resetForm()
  form.parentId = currentNode.value.id
  dialogVisible.value = true
}

function openEdit() {
  if (!currentNode.value) {
    ElMessage.warning('请先选择要编辑的分类')
    return
  }
  dialogMode.value = 'edit'
  form.name = currentNode.value.name
  form.parentId = currentNode.value.parentId
  form.sort = currentNode.value.sort ?? 0
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  submitting.value = true
  try {
    const payload = {
      name: form.name.trim(),
      parentId: form.parentId,
      sort: form.sort
    }
    if (dialogMode.value === 'create') {
      await createCategory(payload)
      ElMessage.success('分类创建成功')
    } else {
      await updateCategory(currentNode.value.id, payload)
      ElMessage.success('分类更新成功')
    }
    dialogVisible.value = false
    currentNode.value = null
    fetchTree()
  } catch (e) {
    // handled
  } finally {
    submitting.value = false
  }
}

async function handleDelete() {
  if (!currentNode.value) {
    ElMessage.warning('请先选择要删除的分类')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定删除分类「${currentNode.value.name}」吗？存在子分类时无法删除。`,
      '提示',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
    await deleteCategory(currentNode.value.id)
    ElMessage.success('删除成功')
    currentNode.value = null
    fetchTree()
  } catch (e) {
    // cancel or error
  }
}

function handleNodeClick(data) {
  currentNode.value = data
}

onMounted(fetchTree)
</script>

<template>
  <el-card shadow="never" v-loading="loading">
    <div class="toolbar">
      <el-button type="primary" @click="openCreateRoot">新增顶级分类</el-button>
      <el-button @click="openCreateChild">新增子分类</el-button>
      <el-button :disabled="!currentNode" @click="openEdit">编辑</el-button>
      <el-button type="danger" :disabled="!currentNode" @click="handleDelete">删除</el-button>
    </div>

    <p v-if="currentNode" class="selected-tip">
      当前选中：<strong>{{ currentNode.name }}</strong>（ID: {{ currentNode.id }}）
    </p>

    <el-tree
      :data="treeData"
      node-key="id"
      :props="treeProps"
      highlight-current
      default-expand-all
      @node-click="handleNodeClick"
    />

    <el-empty v-if="!loading && !treeData.length" description="暂无分类，请新增顶级分类" />

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增分类' : '编辑分类'"
      width="420px"
      destroy-on-close
    >
      <el-form label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="分类名称" />
        </el-form-item>
        <el-form-item label="父级 ID">
          <el-input-number v-model="form.parentId" :min="0" controls-position="right" />
          <span class="form-hint">0 表示顶级分类</span>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" controls-position="right" />
          <span class="form-hint">越小越靠前</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.selected-tip {
  margin: 0 0 12px;
  color: #606266;
  font-size: 14px;
}

.form-hint {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
</style>
