<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { heroApi } from '../api'
import { useUserStore } from '../stores/user'
import { message, Modal } from 'ant-design-vue'

const router = useRouter()
const userStore = useUserStore()
const heroes = ref([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 12, total: 0 })

// 模态框
const modalVisible = ref(false)
const editingHero = ref({ id: null, name: '', role: '', description: '', portrait: '', story: '' })
const isEditing = ref(false)

// 搜索
const searchName = ref('')
const searchRole = ref(null)

// 角色选项
const roleOptions = ['输出型', '重装型', '支援型']

// 检查是否是管理员
const isAdmin = computed(() => {
  return userStore.user?.role === 'ADMIN'
})

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
  fetchHeroes()
})

async function fetchHeroes() {
  loading.value = true
  try {
    const params = {
      page: pagination.value.current,
      size: pagination.value.pageSize,
      name: searchName.value || undefined,
      role: searchRole.value || undefined
    }
    const res = await heroApi.list(params)
    heroes.value = res.data.records || []
    pagination.value.total = res.data.total || 0
  } catch (e) {
    console.error(e)
    message.error('加载失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.value.current = 1
  fetchHeroes()
}

function handleTableChange(pag) {
  pagination.value.current = pag.current
  fetchHeroes()
}

function openAddModal() {
  editingHero.value = { id: null, name: '', role: '', description: '', portrait: '', story: '' }
  isEditing.value = false
  modalVisible.value = true
}

function openEditModal(hero) {
  editingHero.value = { ...hero }
  isEditing.value = true
  modalVisible.value = true
}

async function handleSubmit() {
  if (!editingHero.value.name) {
    message.error('请输入英雄名称')
    return
  }

  try {
    if (isEditing.value) {
      await heroApi.update(editingHero.value.id, editingHero.value)
      message.success('更新成功')
    } else {
      await heroApi.create(editingHero.value)
      message.success('创建成功')
    }
    modalVisible.value = false
    fetchHeroes()
  } catch (e) {
    console.error(e)
    message.error('操作失败')
  }
}

function handleDelete(hero) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除英雄 "${hero.name}" 吗？`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        await heroApi.delete(hero.id)
        message.success('删除成功')
        fetchHeroes()
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
  { title: '类型', dataIndex: 'role', key: 'role' },
  { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
  { title: '头像', dataIndex: 'portrait', key: 'portrait' },
  { title: '操作', key: 'action', width: 150 }
]
</script>

<template>
  <div class="admin-hero">
    <div class="page-header">
      <h1 class="page-title">英雄管理</h1>
    </div>

    <a-card class="content-card">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <a-space>
          <a-input v-model:value="searchName" placeholder="英雄名称" @pressEnter="handleSearch" allowClear style="width: 200px" />
          <a-select v-model:value="searchRole" placeholder="英雄类型" allowClear style="width: 120px">
            <a-select-option v-for="role in roleOptions" :key="role" :value="role">{{ role }}</a-select-option>
          </a-select>
          <a-button type="primary" @click="handleSearch">搜索</a-button>
          <a-button type="primary" @click="openAddModal">新增英雄</a-button>
        </a-space>
      </div>

      <!-- 表格 -->
      <a-table
        :columns="columns"
        :data-source="heroes"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'portrait'">
            <a-image :src="record.portrait" :width="40" :height="40" style="object-fit: cover; border-radius: 4px" fallback="无" />
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
      :title="isEditing ? '编辑英雄' : '新增英雄'"
      @ok="handleSubmit"
      width="600px"
    >
      <a-form :model="editingHero" layout="vertical">
        <a-form-item label="英雄名称" required>
          <a-input v-model:value="editingHero.name" placeholder="输入英雄名称" />
        </a-form-item>
        <a-form-item label="英雄类型">
          <a-select v-model:value="editingHero.role" placeholder="选择类型">
            <a-select-option v-for="role in roleOptions" :key="role" :value="role">{{ role }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model:value="editingHero.description" placeholder="输入描述" :rows="3" />
        </a-form-item>
        <a-form-item label="头像URL">
          <a-input v-model:value="editingHero.portrait" placeholder="输入头像图片URL" />
        </a-form-item>
        <a-form-item label="背景故事">
          <a-textarea v-model:value="editingHero.story" placeholder="输入背景故事" :rows="4" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.admin-hero { padding: 24px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 32px; font-weight: 700; color: var(--text-primary); }
.content-card { background: var(--bg-card) !important; border-color: var(--border-color) !important; }
.search-bar { margin-bottom: 16px; }
</style>
