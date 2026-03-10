<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { mapApi } from '../api'
import { useUserStore } from '../stores/user'
import { message, Modal } from 'ant-design-vue'

const router = useRouter()
const userStore = useUserStore()
const maps = ref([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 12, total: 0 })

// 模态框
const modalVisible = ref(false)
const editingMap = ref({ id: null, name: '', type: '', description: '', image: '' })
const isEditing = ref(false)

// 检查是否是管理员
const isAdmin = computed(() => {
  return userStore.user?.role === 'ADMIN'
})

// 地图类型选项
const typeOptions = ['占领要点', '控制要点', '攻击/护送', '运载目标', '混合地图']

onMounted(() => {
  if (!userStore.token) {
    router.push('/login')
    return
  }
  if (!isAdmin.value) {
    message.error('没有权限访问此页面')
    router.push('/')
    return
  }
  fetchMaps()
})

async function fetchMaps() {
  loading.value = true
  try {
    const params = {
      page: pagination.value.current,
      size: pagination.value.pageSize
    }
    const res = await mapApi.list(params)
    maps.value = res.data.records || []
    pagination.value.total = res.data.total || 0
  } catch (e) {
    console.error(e)
    message.error('加载失败')
  } finally {
    loading.value = false
  }
}

function handleTableChange(pag) {
  pagination.value.current = pag.current
  fetchMaps()
}

function openAddModal() {
  editingMap.value = { id: null, name: '', type: '', description: '', image: '' }
  isEditing.value = false
  modalVisible.value = true
}

function openEditModal(mapItem) {
  editingMap.value = { ...mapItem }
  isEditing.value = true
  modalVisible.value = true
}

async function handleSubmit() {
  if (!editingMap.value.name) {
    message.error('请输入地图名称')
    return
  }

  try {
    if (isEditing.value) {
      await mapApi.update(editingMap.value.id, editingMap.value)
      message.success('更新成功')
    } else {
      await mapApi.create(editingMap.value)
      message.success('创建成功')
    }
    modalVisible.value = false
    fetchMaps()
  } catch (e) {
    console.error(e)
    message.error('操作失败')
  }
}

function handleDelete(mapItem) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除地图 "${mapItem.name}" 吗？`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        await mapApi.delete(mapItem.id)
        message.success('删除成功')
        fetchMaps()
      } catch (e) {
        console.error(e)
        message.error('删除失败')
      }
    }
  })
}

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '类型', dataIndex: 'type', key: 'type' },
  { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
  { title: '图片', dataIndex: 'image', key: 'image' },
  { title: '操作', key: 'action', width: 150 }
]
</script>

<template>
  <div class="admin-map">
    <div class="page-header">
      <h1 class="page-title">地图管理</h1>
    </div>

    <a-card class="content-card">
      <!-- 操作栏 -->
      <div class="action-bar">
        <a-button type="primary" @click="openAddModal">新增地图</a-button>
      </div>

      <!-- 表格 -->
      <a-table
        :columns="columns"
        :data-source="maps"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'image'">
            <a-image :src="record.image" :width="60" :height="40" style="object-fit: cover; border-radius: 4px" fallback="无" />
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button size="small" @click="openEditModal(record)">编辑</a-button>
              <a-button size="small" danger @click="handleDelete(record)">删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEditing ? '编辑地图' : '新增地图'"
      @ok="handleSubmit"
      width="500px"
    >
      <a-form :model="editingMap" layout="vertical">
        <a-form-item label="地图名称" required>
          <a-input v-model:value="editingMap.name" placeholder="输入地图名称" />
        </a-form-item>
        <a-form-item label="地图类型">
          <a-select v-model:value="editingMap.type" placeholder="选择类型">
            <a-select-option v-for="type in typeOptions" :key="type" :value="type">{{ type }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model:value="editingMap.description" placeholder="输入描述" :rows="3" />
        </a-form-item>
        <a-form-item label="图片URL">
          <a-input v-model:value="editingMap.image" placeholder="输入地图图片URL" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.admin-map { padding: 24px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 32px; font-weight: 700; color: var(--text-primary); }
.content-card { background: var(--bg-card) !important; border-color: var(--border-color) !important; }
.action-bar { margin-bottom: 16px; }
</style>
