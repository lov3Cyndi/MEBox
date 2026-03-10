<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { heroApi } from '../api'
import { useThemeStore } from '../stores/theme'
import { useUserStore } from '../stores/user'

const route = useRoute()
const themeStore = useThemeStore()
const userStore = useUserStore()

const hero = ref(null)
const ratings = ref({ ratings: [], stats: {} })
const media = ref([])
const loading = ref(true)
const activeTab = ref('info')
const heroId = computed(() => route.params.id)

const ratingForm = ref({ overallRating: 5, damageRating: 5, utilityRating: 5, survivalRating: 5, comment: '' })

onMounted(async () => {
  loading.value = true
  try {
    const [heroRes, ratingsRes, mediaRes] = await Promise.all([heroApi.get(heroId.value), heroApi.getRatings(heroId.value), heroApi.getMedia(heroId.value)])
    hero.value = heroRes.data
    ratings.value = ratingsRes.data
    media.value = mediaRes.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
})

async function submitRating() {
  if (!userStore.token) {
    message.warning('请先登录后再评分')
    return
  }
  try {
    await heroApi.rate(heroId.value, ratingForm.value)
    message.success('评分提交成功')
    const res = await heroApi.getRatings(heroId.value)
    ratings.value = res.data
  } catch (e) {
    console.error(e)
    message.error(e.response?.data?.message || '评分提交失败')
  }
}

function formatDate(date) { if (!date) return ''; return new Date(date).toLocaleDateString('zh-CN') }
</script>

<template>
  <div class="hero-detail">
    <a-spin :spinning="loading">
      <template v-if="hero">
        <div class="hero-header">
          <div class="hero-portrait"><img :src="hero.portrait || 'https://via.placeholder.com/300x300?text=Hero'" :alt="hero.name" /></div>
          <div class="hero-info">
            <h1 class="hero-name">{{ hero.name }}</h1>
            <p class="hero-realname" v-if="hero.realName">{{ hero.realName }}</p>
            <div class="hero-tags">
              <a-tag :color="themeStore.isOverwatch ? 'blue' : 'red'" size="large">{{ hero.role }}</a-tag>
            </div>
          </div>
        </div>

        <div class="rating-cards">
          <a-card class="rating-card">
            <div class="rating-score"><span class="score-value">{{ ratings.stats?.avgOverall || 0 }}</span><span class="score-label">综合评分</span></div>
          </a-card>
          <a-card class="rating-card">
            <div class="rating-detail">
              <div class="detail-item"><span class="detail-label">输出</span><span class="detail-value">{{ ratings.stats?.avgDamage || 0 }}</span></div>
              <div class="detail-item"><span class="detail-label">生存</span><span class="detail-value">{{ ratings.stats?.avgSurvivability || 0 }}</span></div>
              <div class="detail-item"><span class="detail-label">功能</span><span class="detail-value">{{ ratings.stats?.avgUtility || 0 }}</span></div>
            </div>
          </a-card>
          <a-card class="rating-card">
            <div class="rating-count"><span class="count-value">{{ ratings.stats?.totalCount || 0 }}</span><span class="count-label">评价人数</span></div>
          </a-card>
        </div>

        <a-tabs v-model:activeKey="activeTab" class="hero-tabs">
          <a-tab-pane key="info" tab="英雄信息">
            <a-card title="简介" class="info-card"><p>{{ hero.description || '暂无简介' }}</p></a-card>
            <a-card title="背景故事" class="info-card" v-if="hero.story"><p>{{ hero.story }}</p></a-card>
          </a-tab-pane>
          <a-tab-pane key="media" tab="官方媒体">
            <div class="media-grid" v-if="media.length > 0">
              <a-card v-for="item in media" :key="item.id" class="media-card">
                <template #title><span>{{ item.title }}</span></template>
                <template #extra><a-tag>{{ item.mediaType }}</a-tag></template>
                <p>{{ item.content }}</p>
              </a-card>
            </div>
            <a-empty v-else description="暂无媒体内容" />
          </a-tab-pane>
          <a-tab-pane key="ratings" tab="用户评分">
            <a-card title="我要评分" class="rating-form" v-if="userStore.token">
              <a-form layout="vertical">
                <a-form-item label="综合评分"><a-rate v-model:value="ratingForm.overallRating" /></a-form-item>
                <a-form-item label="评价"><a-textarea v-model:value="ratingForm.comment" placeholder="写下你的评价..." :rows="4" /></a-form-item>
                <a-form-item><a-button type="primary" @click="submitRating">提交评分</a-button></a-form-item>
              </a-form>
            </a-card>
            <a-alert v-else message="登录后可参与评分" type="info" show-icon />
            <div class="ratings-list" v-if="ratings.ratings?.length > 0">
              <a-card v-for="rating in ratings.ratings" :key="rating.id" class="rating-item">
                <div class="rating-header"><a-rate :value="rating.overallRating" disabled /><span class="rating-date">{{ formatDate(rating.createdAt) }}</span></div>
                <p class="rating-comment" v-if="rating.comment">{{ rating.comment }}</p>
              </a-card>
            </div>
          </a-tab-pane>
        </a-tabs>
      </template>
    </a-spin>
  </div>
</template>

<style scoped>
.hero-detail { max-width: 1400px; margin: 0 auto; }
.hero-header { display: flex; gap: 32px; margin-bottom: 24px; padding: 24px; background: var(--bg-card); border-radius: 16px; border: 1px solid var(--border-color); }
.hero-portrait { width: 200px; height: 200px; border-radius: 12px; overflow: hidden; flex-shrink: 0; }
.hero-portrait img { width: 100%; height: 100%; object-fit: cover; }
.hero-info { flex: 1; display: flex; flex-direction: column; justify-content: center; }
.hero-name { font-size: 48px; font-weight: 900; color: var(--text-primary); margin: 0; }
.hero-realname { font-size: 18px; color: var(--text-secondary); margin: 8px 0 16px; }
.hero-tags { margin-bottom: 16px; }
.rating-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 24px; }
.rating-card { background: var(--bg-card) !important; border-color: var(--border-color) !important; text-align: center; }
.rating-score .score-value, .rating-count .count-value { font-size: 48px; font-weight: bold; color: var(--primary-color); display: block; }
.rating-count .count-value { color: var(--accent); }
.rating-detail { display: flex; justify-content: space-around; }
.detail-item { text-align: center; }
.detail-label { display: block; color: var(--text-secondary); font-size: 12px; }
.detail-value { font-size: 24px; font-weight: bold; color: var(--text-primary); }
.hero-tabs :deep(.ant-tabs-nav) { background: var(--bg-card); padding: 0 16px; border-radius: 8px 8px 0 0; }
.hero-tabs :deep(.ant-tabs-tab) { color: var(--text-secondary) !important; }
.hero-tabs :deep(.ant-tabs-tab-active) { color: var(--primary-color) !important; }
.info-card, .media-card { background: var(--bg-card) !important; border-color: var(--border-color) !important; margin-bottom: 16px; }
.info-card p, .media-card p { color: var(--text-secondary); line-height: 1.8; }
.media-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.rating-form { background: var(--bg-card) !important; border-color: var(--border-color) !important; margin-bottom: 24px; }
.ratings-list { display: flex; flex-direction: column; gap: 16px; }
.rating-item { background: var(--bg-card) !important; border-color: var(--border-color) !important; }
.rating-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.rating-date { color: var(--text-secondary); font-size: 12px; }
.rating-comment { color: var(--text-secondary); }
@media (max-width: 768px) { .hero-header { flex-direction: column; align-items: center; } .hero-portrait { width: 150px; height: 150px; } .rating-cards { grid-template-columns: 1fr; } .media-grid { grid-template-columns: 1fr; } }
</style>
