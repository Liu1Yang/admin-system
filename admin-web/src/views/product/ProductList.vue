<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryTree } from '../../api/category'
import { deleteProduct, getProductPage, updateProductStatus } from '../../api/product'
import { flattenCategories } from '../../utils/category'
import { hasPermission } from '../../utils/auth'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const categoryOptions = ref([])

const query = reactive({
  name: '',
  categoryId: undefined,
  status: undefined,
  minPrice: undefined,
  maxPrice: undefined,
  page: 1,
  size: 10
})

const pagination = reactive({  // pagination:分页
  total: 0,
  current: 1,
  size: 10,
  pages: 0
})

const canDelete = hasPermission('product:delete')

function goCreate() {
  router.push('/products/create')
}

function goEdit(row) {
  router.push(`/products/${row.id}/edit`)
}

async function loadCategories() {
  try {
    const res = await getCategoryTree()
    categoryOptions.value = flattenCategories(res.data || [])
  } catch (e) {
    // handled
  }
}

async function fetchList() {
  loading.value = true
  try {
    const params = {
      page: query.page,
      size: query.size,
      name: query.name || undefined,
      categoryId: query.categoryId || undefined,
      status: query.status ?? undefined,
      minPrice: query.minPrice ?? undefined,
      maxPrice: query.maxPrice ?? undefined
    }
    const res = await getProductPage(params)
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
  query.name = ''
  query.categoryId = undefined
  query.status = undefined
  query.minPrice = undefined
  query.maxPrice = undefined
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
    await ElMessageBox.confirm(`确定删除商品「${row.name}」吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && query.page > 1) {
      query.page -= 1
    }
    fetchList()
  } catch (e) {
    // cancel or error
  }
}

async function handleToggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  const action = nextStatus === 1 ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(`确定${action}商品「${row.name}」吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await updateProductStatus(row.id, nextStatus)
    ElMessage.success(`${action}成功`)
    fetchList()
  } catch (e) {
    // cancel or error
  }
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}

function formatPrice(value) {
  if (value == null) return '-'
  return `¥${Number(value).toFixed(2)}`
}

onMounted(async () => {
  await loadCategories()
  fetchList()
})
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-button type="primary" @click="goCreate">新增商品</el-button>
    </div>

    <el-form :inline="true" @submit.prevent="handleSearch">
      <el-form-item label="商品名称">
        <el-input
          v-model="query.name"
          placeholder="模糊搜索"
          clearable
          style="width: 160px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="分类">
        <el-select
          v-model="query.categoryId"
          placeholder="全部"
          clearable
          filterable
          style="width: 180px"
        >
          <el-option
            v-for="item in categoryOptions"
            :key="item.id"
            :label="item.label"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="价格">
        <el-input-number
          v-model="query.minPrice"
          :min="0"
          :precision="2"
          placeholder="最低"
          controls-position="right"
          style="width: 120px"
        />
        <span class="price-sep">-</span>
        <el-input-number
          v-model="query.maxPrice"
          :min="0"
          :precision="2"
          placeholder="最高"
          controls-position="right"
          style="width: 120px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <el-image
            v-if="row.coverUrl"
            :src="row.coverUrl"
            fit="cover"
            class="cover-img"
            :preview-src-list="[row.coverUrl]"
          />
          <span v-else class="no-cover">无</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" min-width="100" />
      <el-table-column label="售价" width="100">
        <template #default="{ row }">{{ formatPrice(row.price) }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="goEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
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
  </el-card>
</template>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}

.price-sep {
  margin: 0 6px;
  color: #909399;
}

.cover-img {
  width: 48px;
  height: 48px;
  border-radius: 4px;
}

.no-cover {
  color: #c0c4cc;
  font-size: 12px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
