<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { videoApi, heroApi, mapApi } from '../api'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'

const router = useRouter()
const userStore = useUserStore()
const heroes = ref([])
const maps = ref([])
const loading = ref(false)

const form = ref({ title: '', description: '', url: '', thumbnail: '', heroId: null, mapId: null, gameType: '快速匹配' })
const gameTypes = ['快速匹配', '竞技比赛', '街机模式', '自定义游戏']

onMounted(async () => {
  if (!userStore.token) { router.push('/login'); return }
  try { const [heroesRes, mapsRes] = await Promise.all([heroApi.list({ page: 1, size: 100 }), mapApi.list({ page: 1, size: 100 })]); heroes.value = heroesRes.data.records || []; maps.value = mapsRes.data.records || []; }
  catch (e) { console.error(e) }
})

async function handleSubmit() {
  if (!form.value.title || !form.value.url) { message.error('请填写标题和视频链接'); return }
  loading.value = true
  try { await videoApi.upload(form.value); message.success('上传成功'); router.push('/videos'); }
  catch (e) { console.error(e); message.error('上传失败'); }
  finally { loading.value = false }
}
</script>

<template>
  <div class="video-upload">
    <div class="page-header"><h1 class="page-title">上传视频</h1></div>
    <a-card class="upload-card">
      <a-form :model="form" layout="vertical" @finish="handleSubmit">
        <a-form-item label="标题" required><a-input v-model:value="form.title" placeholder="输入视频标题" /></a-form-item>
        <a-form-item label="视频链接" required><a-input v-model:value="form.url" placeholder="输入视频URL" /></a-form-item>
        <a-form-item label="封面图片"><a-input v-model:value="form.thumbnail" placeholder="输入封面图片URL" /></a-form-item>
        <a-form-item label="视频描述"><a-textarea v-model:value="form.description" placeholder="输入视频描述" :rows="4" /></a-form-item>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="使用英雄"><a-select v-model:value="form.heroId" placeholder="选择英雄" allowClear><a-select-option v-for="hero in heroes" :key="hero.id" :value="hero.id">{{ hero.name }}</a-select-option></a-select></a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="游戏地图"><a-select v-model:value="form.mapId" placeholder="选择地图" allowClear><a-select-option v-for="map in maps" :key="map.id" :value="map.id">{{ map.name }}</a-select-option></a-select></a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="游戏模式"><a-select v-model:value="form.gameType"><a-select-option v-for="type in gameTypes" :key="type" :value="type">{{ type }}</a-select-option></a-select></a-form-item>
          </a-col>
        </a-row>
        <a-form-item><a-button type="primary" html-type="submit" :loading="loading">上传视频</a-button></a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<style scoped>
.video-upload { max-width: 800px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 32px; font-weight: 700; color: var(--text-primary); }
.upload-card { background: var(--bg-card) !important; border-color: var(--border-color) !important; }
</style>
