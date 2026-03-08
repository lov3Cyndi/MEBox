<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { videoApi } from '../api'
import { useThemeStore } from '../stores/theme'
import { PlayCircleOutlined, UserOutlined, EyeOutlined, HeartOutlined, MessageOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const themeStore = useThemeStore()
const videos = ref([])
const loading = ref(true)

onMounted(async () => {
  try { const res = await videoApi.list({ page: 1, size: 50 }); videos.value = res.data.records || []; }
  catch (e) { console.error(e) }
  finally { loading.value = false }
})

function goVideo(id) { router.push(`/videos/${id}`) }
</script>

<template>
  <div class="video-list">
    <div class="page-header"><h1 class="page-title">全场最佳</h1><p class="page-desc">分享你的精彩操作</p></div>
    <a-spin :spinning="loading">
      <div class="video-grid" v-if="videos.length > 0">
        <a-card v-for="video in videos" :key="video.id" class="video-card" hoverable @click="goVideo(video.id)">
          <template #cover>
            <div class="video-thumbnail">
              <img :src="video.thumbnail || 'https://via.placeholder.com/400x225?text=Video'" :alt="video.title" />
              <div class="play-icon"><PlayCircleOutlined /></div>
            </div>
          </template>
          <a-card-meta>
            <template #title><div class="video-title">{{ video.title }}</div></template>
            <template #description>
              <div class="video-meta">
                <span class="video-uploader"><UserOutlined /> {{ video.uploader?.username || '未知用户' }}</span>
                <span class="video-stats"><EyeOutlined /> {{ video.viewCount || 0 }} <HeartOutlined /> {{ video.likeCount || 0 }}</span>
              </div>
            </template>
          </a-card-meta>
        </a-card>
      </div>
      <a-empty v-else description="暂无视频数据" />
    </a-spin>
  </div>
</template>

<style scoped>
.video-list { max-width: 1400px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 32px; font-weight: 700; color: var(--text-primary); margin: 0; }
.page-desc { color: var(--text-secondary); margin-top: 8px; }
.video-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.video-card { transition: transform 0.3s; background: var(--bg-card) !important; border-color: var(--border-color) !important; }
.video-card:hover { transform: translateY(-8px); }
.video-thumbnail { position: relative; width: 100%; aspect-ratio: 16/9; overflow: hidden; background: var(--bg-secondary); }
.video-thumbnail img { width: 100%; height: 100%; object-fit: cover; }
.play-icon { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); font-size: 48px; color: white; opacity: 0.8; }
.video-title { color: var(--text-primary) !important; font-size: 16px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.video-meta { display: flex; justify-content: space-between; align-items: center; }
.video-uploader, .video-stats { color: var(--text-secondary); font-size: 12px; }
@media (max-width: 1024px) { .video-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .video-grid { grid-template-columns: 1fr; } }
</style>
