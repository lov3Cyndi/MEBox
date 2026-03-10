<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { videoApi } from '../api'
import { useUserStore } from '../stores/user'
import { HeartOutlined, HeartFilled, UserOutlined, EyeOutlined } from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const video = ref(null)
const comments = ref([])
const loading = ref(true)
const commentText = ref('')
const liked = ref(false)
const videoId = computed(() => route.params.id)

onMounted(async () => {
  loading.value = true
  try { const [videoRes, commentsRes] = await Promise.all([videoApi.get(videoId.value), videoApi.getComments(videoId.value)]); video.value = videoRes.data; comments.value = commentsRes.data || []; }
  catch (e) { console.error(e) }
  finally { loading.value = false }
})

async function toggleLike() {
  if (!userStore.token) { router.push('/login'); return }
  try { liked.value ? await videoApi.unlike(videoId.value) : await videoApi.like(videoId.value); liked.value = !liked.value; const res = await videoApi.get(videoId.value); video.value = res.data; }
  catch (e) { console.error(e) }
}

async function submitComment() {
  if (!userStore.token) { router.push('/login'); return }
  if (!commentText.value.trim()) {
    message.warning('请输入评论内容')
    return
  }
  try {
    await videoApi.addComment(videoId.value, { content: commentText.value })
    message.success('评论发布成功')
    commentText.value = ''
    const res = await videoApi.getComments(videoId.value)
    comments.value = res.data || []
  } catch (e) {
    console.error(e)
    message.error(e.response?.data?.message || '评论发布失败')
  }
}

function formatDate(date) { if (!date) return ''; return new Date(date).toLocaleDateString('zh-CN') }
</script>

<template>
  <div class="video-detail">
    <a-spin :spinning="loading">
      <template v-if="video">
        <div class="video-player"><div class="video-wrapper"><iframe :src="video.url" frameborder="0" allowfullscreen class="video-iframe"></iframe></div></div>
        <div class="video-info">
          <h1 class="video-title">{{ video.title }}</h1>
          <div class="video-meta">
            <span class="meta-item"><UserOutlined /> {{ video.uploader?.username || '未知用户' }}</span>
            <span class="meta-item"><EyeOutlined /> {{ video.viewCount || 0 }} 次观看</span>
            <span class="meta-item">{{ formatDate(video.createdAt) }}</span>
          </div>
          <div class="video-actions">
            <a-button :type="liked ? 'primary' : 'default'" @click="toggleLike"><HeartFilled v-if="liked" /><HeartOutlined v-else /> {{ video.likeCount || 0 }} 点赞</a-button>
          </div>
          <a-card title="视频描述" class="description-card" v-if="video.description"><p>{{ video.description }}</p></a-card>
        </div>
        <div class="comments-section">
          <h2 class="section-title">评论 ({{ comments.length }})</h2>
          <div class="comment-form" v-if="userStore.token">
            <a-textarea v-model:value="commentText" placeholder="写下你的评论..." :rows="3" />
            <a-button type="primary" @click="submitComment" class="submit-btn">发表评论</a-button>
          </div>
          <a-alert v-else message="登录后可参与评论" type="info" show-icon />
          <div class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-avatar"><UserOutlined /></div>
              <div class="comment-content">
                <div class="comment-header"><span class="comment-user">{{ comment.user?.username || '未知用户' }}</span><span class="comment-date">{{ formatDate(comment.createdAt) }}</span></div>
                <p class="comment-text">{{ comment.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </template>
    </a-spin>
  </div>
</template>

<style scoped>
.video-detail { max-width: 1200px; margin: 0 auto; }
.video-player { margin-bottom: 24px; }
.video-wrapper { position: relative; padding-bottom: 56.25%; height: 0; background: #000; border-radius: 12px; overflow: hidden; }
.video-iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }
.video-info { margin-bottom: 24px; }
.video-title { font-size: 28px; font-weight: 700; color: var(--text-primary); margin: 0 0 16px; }
.video-meta { display: flex; gap: 24px; color: var(--text-secondary); margin-bottom: 16px; }
.meta-item { display: flex; align-items: center; gap: 8px; }
.video-actions { margin-bottom: 16px; }
.description-card { background: var(--bg-card) !important; border-color: var(--border-color) !important; }
.description-card p { color: var(--text-secondary); line-height: 1.8; }
.comments-section { margin-top: 32px; }
.section-title { font-size: 24px; font-weight: 600; color: var(--text-primary); margin-bottom: 16px; }
.comment-form { margin-bottom: 24px; }
.submit-btn { margin-top: 12px; }
.comments-list { display: flex; flex-direction: column; gap: 16px; }
.comment-item { display: flex; gap: 12px; padding: 16px; background: var(--bg-card); border-radius: 8px; border: 1px solid var(--border-color); }
.comment-avatar { width: 40px; height: 40px; border-radius: 50%; background: var(--bg-secondary); display: flex; align-items: center; justify-content: center; color: var(--text-secondary); }
.comment-content { flex: 1; }
.comment-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.comment-user { font-weight: 600; color: var(--text-primary); }
.comment-date { color: var(--text-secondary); font-size: 12px; }
.comment-text { color: var(--text-secondary); margin: 0; }
</style>
