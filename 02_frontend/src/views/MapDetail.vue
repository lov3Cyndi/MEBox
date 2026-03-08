<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { mapApi } from '../api'
import { useThemeStore } from '../stores/theme'
import { useUserStore } from '../stores/user'

const route = useRoute()
const themeStore = useThemeStore()
const userStore = useUserStore()

const map = ref(null)
const ratings = ref({ ratings: [], stats: {} })
const loading = ref(true)
const mapId = computed(() => route.params.id)

const ratingForm = ref({ rating: 5, comment: '' })

onMounted(async () => {
  loading.value = true
  try { const [mapRes, ratingsRes] = await Promise.all([mapApi.get(mapId.value), mapApi.getRatings(mapId.value)]); map.value = mapRes.data; ratings.value = ratingsRes.data; }
  catch (e) { console.error(e) }
  finally { loading.value = false }
})

async function submitRating() {
  if (!userStore.token) return
  try { await mapApi.rate(mapId.value, ratingForm.value); const res = await mapApi.getRatings(mapId.value); ratings.value = res.data; }
  catch (e) { console.error(e) }
}

function formatDate(date) { if (!date) return ''; return new Date(date).toLocaleDateString('zh-CN') }
</script>

<template>
  <div class="map-detail">
    <a-spin :spinning="loading">
      <template v-if="map">
        <div class="map-header">
          <div class="map-image"><img :src="map.image || 'https://via.placeholder.com/800x400?text=Map'" :alt="map.name" /></div>
          <div class="map-info">
            <h1 class="map-name">{{ map.name }}</h1>
            <div class="map-tags"><a-tag :color="themeStore.isOverwatch ? 'blue' : 'red'" size="large">{{ map.type || '未知类型' }}</a-tag></div>
          </div>
        </div>
        <div class="rating-overview">
          <a-card class="rating-card"><div class="rating-score"><span class="score-value">{{ ratings.stats?.avgRating || 0 }}</span><span class="score-label">平均评分</span></div></a-card>
          <a-card class="rating-card"><div class="rating-count"><span class="count-value">{{ ratings.stats?.totalCount || 0 }}</span><span class="count-label">评价人数</span></div></a-card>
        </div>
        <a-card title="地图简介" class="description-card"><p>{{ map.description || '暂无简介' }}</p></a-card>
        <div class="rating-section">
          <a-card title="我要评分" class="rating-form" v-if="userStore.token">
            <a-form layout="vertical">
              <a-form-item label="评分"><a-rate v-model:value="ratingForm.rating" /></a-form-item>
              <a-form-item label="评价"><a-textarea v-model:value="ratingForm.comment" placeholder="写下你的评价..." :rows="4" /></a-form-item>
              <a-form-item><a-button type="primary" @click="submitRating">提交评分</a-button></a-form-item>
            </a-form>
          </a-card>
          <a-alert v-else message="登录后可参与评分" type="info" show-icon />
        </div>
      </template>
    </a-spin>
  </div>
</template>

<style scoped>
.map-detail { max-width: 1200px; margin: 0 auto; }
.map-header { position: relative; margin-bottom: 24px; }
.map-image { width: 100%; height: 400px; border-radius: 16px; overflow: hidden; }
.map-image img { width: 100%; height: 100%; object-fit: cover; }
.map-info { position: absolute; bottom: 0; left: 0; right: 0; padding: 24px; background: linear-gradient(transparent, rgba(0, 0, 0, 0.8)); border-radius: 0 0 16px 16px; }
.map-name { font-size: 36px; font-weight: 700; color: var(--text-primary); margin: 0; }
.rating-overview { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; margin-bottom: 24px; }
.rating-card { background: var(--bg-card) !important; border-color: var(--border-color) !important; text-align: center; }
.rating-score .score-value, .rating-count .count-value { font-size: 48px; font-weight: bold; color: var(--primary-color); display: block; }
.rating-count .count-value { color: var(--accent); }
.description-card { background: var(--bg-card) !important; border-color: var(--border-color) !important; margin-bottom: 24px; }
.description-card p { color: var(--text-secondary); line-height: 1.8; }
.rating-form { background: var(--bg-card) !important; border-color: var(--border-color) !important; }
@media (max-width: 768px) { .map-image { height: 250px; } .map-name { font-size: 24px; } .rating-overview { grid-template-columns: 1fr; } }
</style>
