<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { heroApi } from '../api'
import { useThemeStore } from '../stores/theme'
import { TeamOutlined, GlobalOutlined, PlayCircleOutlined, SearchOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const themeStore = useThemeStore()

const heroes = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await heroApi.list({ page: 1, size: 100 })
    heroes.value = res.data.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

function goHero(id) {
  router.push(`/heroes/${id}`)
}

function goHeroes() {
  router.push('/heroes')
}

function goMaps() {
  router.push('/maps')
}

function goVideos() {
  router.push('/videos')
}

function searchHeroes() {
  router.push({ path: '/heroes', query: { name: '' } })
}
</script>

<template>
  <div class="home">
    <div class="hero-banner">
      <div class="banner-content">
        <h1 class="banner-title">MEBox</h1>
        <p class="banner-subtitle">守望先锋内容社区</p>
      </div>
    </div>

    <div class="quick-links">
      <a-card class="quick-card" @click="goHeroes">
        <div class="quick-card-content">
          <TeamOutlined class="quick-icon" />
          <span class="quick-label">英雄</span>
        </div>
      </a-card>
      <a-card class="quick-card" @click="goMaps">
        <div class="quick-card-content">
          <GlobalOutlined class="quick-icon" />
          <span class="quick-label">地图</span>
        </div>
      </a-card>
      <a-card class="quick-card" @click="goVideos">
        <div class="quick-card-content">
          <PlayCircleOutlined class="quick-icon" />
          <span class="quick-label">视频</span>
        </div>
      </a-card>
    </div>

    <div class="section">
      <div class="section-header">
        <h2 class="section-title">英雄一览</h2>
        <a-button type="link" @click="goHeroes" class="view-all">
          查看全部 <span class="arrow">→</span>
        </a-button>
      </div>

      <a-spin :spinning="loading">
        <div class="hero-grid">
          <a-card
            v-for="hero in heroes.slice(0, 8)"
            :key="hero.id"
            class="hero-card"
            hoverable
            @click="goHero(hero.id)"
          >
            <template #cover>
              <div class="hero-image">
                <img :src="hero.portrait || 'https://via.placeholder.com/200x200?text=Hero'" :alt="hero.name" />
              </div>
            </template>
            <a-card-meta>
              <template #title>
                <div class="hero-name">{{ hero.name }}</div>
              </template>
              <template #description>
                <a-tag :color="themeStore.isOverwatch ? 'blue' : 'red'">{{ hero.role }}</a-tag>
              </template>
            </a-card-meta>
          </a-card>
        </div>
      </a-spin>
    </div>
  </div>
</template>

<style scoped>
.home { max-width: 1400px; margin: 0 auto; }
.hero-banner { position: relative; height: 300px; background: linear-gradient(135deg, var(--bg-secondary) 0%, var(--bg-color) 100%); border-radius: 16px; overflow: hidden; margin-bottom: 32px; }
.hero-banner::before { content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: radial-gradient(circle at 30% 50%, var(--primary-color) 0%, transparent 50%), radial-gradient(circle at 70% 80%, var(--accent) 0%, transparent 40%); opacity: 0.15; }
.banner-content { position: relative; height: 100%; display: flex; flex-direction: column; justify-content: center; align-items: center; z-index: 1; }
.banner-title { font-size: 64px; font-weight: 900; background: linear-gradient(135deg, var(--primary-color), var(--accent)); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; margin: 0; letter-spacing: 8px; }
.banner-subtitle { font-size: 18px; color: var(--text-secondary); margin-top: 8px; }
.quick-links { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 32px; }
.quick-card { cursor: pointer; transition: transform 0.3s; }
.quick-card:hover { transform: translateY(-4px); }
.quick-card-content { display: flex; align-items: center; justify-content: center; gap: 12px; padding: 24px; }
.quick-icon { font-size: 32px; color: var(--primary-color); }
.quick-label { font-size: 20px; font-weight: 600; color: var(--text-primary); }
.section { margin-bottom: 32px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.section-title { font-size: 28px; font-weight: 700; color: var(--text-primary); }
.view-all { color: var(--primary-color) !important; }
.hero-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.hero-card { transition: transform 0.3s; background: var(--bg-card) !important; border-color: var(--border-color) !important; }
.hero-card:hover { transform: translateY(-8px); }
.hero-image { width: 100%; aspect-ratio: 1; overflow: hidden; background: var(--bg-secondary); }
.hero-image img { width: 100%; height: 100%; object-fit: cover; }
.hero-name { text-align: center; color: var(--text-primary); }
@media (max-width: 1024px) { .hero-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .hero-grid { grid-template-columns: repeat(2, 1fr); } .quick-links { grid-template-columns: 1fr; } }
</style>
