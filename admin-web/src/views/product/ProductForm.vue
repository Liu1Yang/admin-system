<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryTree } from '../../api/category'
import { uploadFile } from '../../api/file'
import {
  createProduct,
  getProductById,
  updateProduct,
  updateProductStatus,
  uploadProductCover
} from '../../api/product'
import { flattenCategories } from '../../utils/category'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const uploading = ref(false)
const categoryOptions = ref([])
const productStatus = ref(0)

const isEdit = computed(() => !!route.params.id)
const pageTitle = computed(() => (isEdit.value ? '编辑商品' : '新增商品'))

const form = reactive({
  name: '',
  categoryId: undefined,
  price: undefined,
  stock: 0,
  coverUrl: '',
  description: ''
})

async function loadCategories() {
  const res = await getCategoryTree()
  categoryOptions.value = flattenCategories(res.data || [])
}

async function loadProduct() {
  if (!isEdit.value) return
  loading.value = true
  try {
    const res = await getProductById(route.params.id)
    const p = res.data
    form.name = p.name
    form.categoryId = p.categoryId
    form.price = Number(p.price)
    form.stock = p.stock
    form.coverUrl = p.coverUrl || ''
    form.description = p.description || ''
    productStatus.value = p.status
  } catch (e) {
    router.push('/products')
  } finally {
    loading.value = false
  }
}

function validateForm() {
  if (!form.name?.trim()) {
    ElMessage.warning('请输入商品名称')
    return false
  }
  if (!form.categoryId) {
    ElMessage.warning('请选择分类')
    return false
  }
  if (!form.price || form.price <= 0) {
    ElMessage.warning('售价必须大于 0')
    return false
  }
  if (form.stock == null || form.stock < 0) {
    ElMessage.warning('库存不能为负数')
    return false
  }
  return true
}

async function handleSubmit() {
  if (!validateForm()) return
  submitting.value = true
  try {
    const payload = {
      name: form.name.trim(),
      categoryId: form.categoryId,
      price: form.price,
      stock: form.stock,
      coverUrl: form.coverUrl || undefined,
      description: form.description || undefined
    }
    if (isEdit.value) {
      await updateProduct(route.params.id, payload)
      ElMessage.success('保存成功')
    } else {
      await createProduct(payload)
      ElMessage.success('创建成功，默认下架')
    }
    router.push('/products')
  } catch (e) {
    // handled
  } finally {
    submitting.value = false
  }
}

async function handleCoverUpload(options) {
  uploading.value = true
  try {
    let res
    if (isEdit.value) {
      res = await uploadProductCover(route.params.id, options.file)
      form.coverUrl = res.data.coverUrl
    } else {
      res = await uploadFile(options.file)
      form.coverUrl = res.data.url
    }
    ElMessage.success('封面上传成功')
    options.onSuccess?.(res.data)
  } catch (e) {
    options.onError?.(e)
  } finally {
    uploading.value = false
  }
}

function beforeCoverUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

async function handleToggleStatus() {
  const nextStatus = productStatus.value === 1 ? 0 : 1
  const action = nextStatus === 1 ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(`确定${action}该商品吗？`, '提示', { type: 'warning' })
    await updateProductStatus(route.params.id, nextStatus)
    productStatus.value = nextStatus
    ElMessage.success(`${action}成功`)
  } catch (e) {
    // cancel or error
  }
}

function goBack() {
  router.push('/products')
}

onMounted(async () => {
  await loadCategories()
  await loadProduct()
})
</script>

<template>
  <el-card v-loading="loading" shadow="never">
    <div class="form-header">
      <h2>{{ pageTitle }}</h2>
      <el-button @click="goBack">返回列表</el-button>
    </div>

    <el-form label-width="90px" class="product-form">
      <el-form-item label="商品名称" required>
        <el-input v-model="form.name" placeholder="请输入商品名称" maxlength="100" />
      </el-form-item>

      <el-form-item label="分类" required>
        <el-select v-model="form.categoryId" placeholder="请选择分类" filterable style="width: 320px">
          <el-option
            v-for="item in categoryOptions"
            :key="item.id"
            :label="item.label"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="售价" required>
        <el-input-number v-model="form.price" :min="0.01" :precision="2" controls-position="right" />
      </el-form-item>

      <el-form-item label="库存" required>
        <el-input-number v-model="form.stock" :min="0" controls-position="right" />
      </el-form-item>

      <el-form-item label="封面图">
        <div class="cover-row">
          <el-upload
            :show-file-list="false"
            :http-request="handleCoverUpload"
            :before-upload="beforeCoverUpload"
            accept="image/*"
          >
            <el-button :loading="uploading">{{ form.coverUrl ? '更换封面' : '上传封面' }}</el-button>
          </el-upload>
          <el-image
            v-if="form.coverUrl"
            :src="form.coverUrl"
            fit="cover"
            class="cover-preview"
            :preview-src-list="[form.coverUrl]"
          />
          <span v-else class="cover-hint">支持 jpg/png，≤ 2MB</span>
        </div>
        <p v-if="form.coverUrl" class="cover-url">{{ form.coverUrl }}</p>
      </el-form-item>

      <el-form-item label="描述">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="商品描述（可选）"
        />
      </el-form-item>

      <el-form-item v-if="isEdit" label="状态">
        <el-tag :type="productStatus === 1 ? 'success' : 'info'">
          {{ productStatus === 1 ? '上架' : '下架' }}
        </el-tag>
        <el-button type="primary" link class="status-btn" @click="handleToggleStatus">
          {{ productStatus === 1 ? '下架' : '上架' }}
        </el-button>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
        <el-button @click="goBack">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<style scoped>
.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.form-header h2 {
  margin: 0;
  font-size: 18px;
}

.product-form {
  max-width: 640px;
}

.cover-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.cover-preview {
  width: 80px;
  height: 80px;
  border-radius: 4px;
}

.cover-hint {
  color: #909399;
  font-size: 13px;
}

.cover-url {
  margin: 8px 0 0;
  color: #909399;
  font-size: 12px;
  word-break: break-all;
}

.status-btn {
  margin-left: 12px;
}
</style>
