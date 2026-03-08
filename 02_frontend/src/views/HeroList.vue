<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { heroApi } from '../api'
import { useThemeStore } from '../stores/theme'

const router = useRouter()
const route = useRoute()
const themeStore = useThemeStore()

const heroes = ref([])
const roles = ref([])
const loading = ref(true)

const filters = ref({ page: 1, size: 12, role: '', name: '' })

onMounted(async () => {
  try {
    const rolesRes = await heroApi.getRoles()
    roles.value = rolesRes.data || []
    filters.value.role = route.query.role || ''
    filters.value.name = route.query.name || ''
    await fetchHeroes()
  } catch (e) { console.error(e) }
  finally { loading.value = false }
})

watch(() => route.query, async () => {
  filters.value.role = route.query.role || ''
  filters.value.name = route.query.name || ''
  await fetchHeroes()
}, { deep: true })

async function fetchHeroes() {
  loading.value = true
  try { const res = await heroApi.list(filters.value); heroes.value = res.data.records || []; }
  catch (e) { console.error(e) }
  finally { loading.value = false }
}

function goHero(id) { router.push(`/heroes/${id}`) }
function handleRoleFilter(role) { filters.value.role = filters.value.role === role ? '' : role; router.push({ path: '/heroes', query: { ...route.query, role: filters.value.role } }) }
function handleSearch() { router.push({ path: '/heroes', query: { ...route.query, name: filters.value.name } }) }
</script>

<template>
  <div class="hero-list">
    <div class="page-header"><h1 class="page-title">英雄列表</h1></div>
    <div class="filter-section">
      <div class="role-filters">
        <a-button v-for="role in roles" :key="role" :type="filters.value.role === role ? 'primary' : 'default'" class="role-btn" @click="handleRoleFilter(role)">{{ role }}</a-button>
      </div>
    </div>
    <a-spin :spinning="loading">
      <div class="hero-grid" v-if="heroes.length > 0">
        <a-card v-for="hero in heroes" :key="hero.id" class="hero-card" hoverable @click="goHero(hero.id)">
          <template #cover>
            <div class="hero-image"><img :src="hero.portrait || 'https://via.placeholder.com/200x200?text=Hero'" :alt="hero.name" /></div>
          </template>
          <a-card-meta>
            <template #title><div class="hero-name">{{ hero.name }}</div></template>
            <template #description><a-tag :color="themeStore.isOverwatch ? 'blue' : 'red'">{{ hero.role }}</a-tag></template>
          </a-card-meta>
        </a-card>
      </div>
      <a-empty v-else description="暂无英雄数据" />
    </a-spin>
  </div>
</template>

<style scoped>
.hero-list { max-width: 1400px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 32px; font-weight: 700; color: var(--text-primary); }
.filter-section { margin-bottom: 24px; }
.role-filters { display: flex; gap: 12px; flex-wrap: wrap; }
.role-btn { background: var(--bg-card) !important; border-color: var(--border-color) !important; color: var(--text-secondary) !important; }
.role-btn:hover { border-color: var(--primary-color) !important; color: var(--primary-color) !important; }
.role-btn.ant-btn-primary { background: var(--primary-color) !important; border-color: var(--primary-color) !important; color: white !important; }
.hero-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.hero-card { transition: transform 0.3s; background: var(--bg-card) !important; border-color: var(--border-color) !important; }
.hero-card:hover { transform: translateY(-8px); }
.hero-image { width: 100%; aspect-ratio: 1; overflow: hidden; background: var(--bg-secondary); }
.hero-image img { width: 100%; height: 100%; object-fit: cover; }
.hero-name { text-align: center; color: var(--text-primary) !important; font-size: 18px; }
@media (max-width: 1024px) { .hero-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .hero-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
