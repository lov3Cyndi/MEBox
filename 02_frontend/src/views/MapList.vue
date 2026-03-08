<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { mapApi } from '../api'
import { useThemeStore } from '../stores/theme'

const router = useRouter()
const themeStore = useThemeStore()
const maps = ref([])
const loading = ref(true)

onMounted(async () => {
  try { const res = await mapApi.list({ page: 1, size: 50 }); maps.value = res.data.records || []; }
  catch (e) { console.error(e) }
  finally { loading.value = false }
})

function goMap(id) { router.push(`/maps/${id}`) }
</script>

<template>
  <div class="map-list">
    <div class="page-header"><h1 class="page-title">游戏地图</h1></div>
    <a-spin :spinning="loading">
      <div class="map-grid" v-if="maps.length > 0">
        <a-card v-for="map in maps" :key="map.id" class="map-card" hoverable @click="goMap(map.id)">
          <template #cover>
            <div class="map-image"><img :src="map.image || 'https://via.placeholder.com/400x200?text=Map'" :alt="map.name" /></div>
          </template>
          <a-card-meta>
            <template #title><div class="map-name">{{ map.name }}</div></template>
            <template #description><a-tag :color="themeStore.isOverwatch ? 'blue' : 'red'">{{ map.type || '未知类型' }}</a-tag></template>
          </a-card-meta>
        </a-card>
      </div>
      <a-empty v-else description="暂无地图数据" />
    </a-spin>
  </div>
</template>

<style scoped>
.map-list { max-width: 1400px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 32px; font-weight: 700; color: var(--text-primary); }
.map-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.map-card { transition: transform 0.3s; background: var(--bg-card) !important; border-color: var(--border-color) !important; }
.map-card:hover { transform: translateY(-8px); }
.map-image { width: 100%; height: 180px; overflow: hidden; background: var(--bg-secondary); }
.map-image img { width: 100%; height: 100%; object-fit: cover; }
.map-name { text-align: center; color: var(--text-primary) !important; }
@media (max-width: 1024px) { .map-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .map-grid { grid-template-columns: 1fr; } }
</style>
