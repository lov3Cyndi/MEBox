<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { videoApi, heroApi, mapApi } from '../api'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import { UploadOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const heroes = ref([])
const maps = ref([])
const loading = ref(false)
const uploading = ref(false)

const form = ref({ title: '', description: '', url: '', thumbnail: '', heroId: null, mapId: null, gameType: '快速匹配' })
const gameTypes = ['快速匹配', '竞技比赛', '街机模式', '自定义游戏']

// 文件上传相关
const fileList = ref([])
const videoFile = ref(null)

onMounted(async () => {
  if (!userStore.token) { router.push('/login'); return }
  try { const [heroesRes, mapsRes] = await Promise.all([heroApi.list({ page: 1, size: 100 }), mapApi.list({ page: 1, size: 100 })]); heroes.value = heroesRes.data.records || []; maps.value = mapsRes.data.records || []; }
  catch (e) { console.error(e) }
})

// 文件选择
function handleFileChange(info) {
  if (info.file.status === 'done') {
    videoFile.value = info.file.originFileObj
    message.success('文件上传成功')
  } else if (info.file.status === 'error') {
    message.error('文件上传失败')
  }
}

// 上传前检查
function beforeUpload(file) {
  const isVideo = file.type.startsWith('video/')
  if (!isVideo) {
    message.error('只能上传视频文件!')
    return false
  }
  const isLt500M = file.size / 1024 / 1024 < 500
  if (!isLt500M) {
    message.error('视频大小不能超过 500MB!')
    return false
  }
  videoFile.value = file
  return false // 手动上传
}

async function handleSubmit() {
  if (!form.value.title) { message.error('请填写标题'); return }
  if (!form.value.url && !videoFile.value) { message.error('请填写视频链接或上传视频文件'); return }

  loading.value = true
  try {
    let url = form.value.url

    // 如果有文件，先上传文件
    if (videoFile.value) {
      uploading.value = true
      const formData = new FormData()
      formData.append('file', videoFile.value)
      formData.append('title', form.value.title)
      if (form.value.description) formData.append('description', form.value.description)
      if (form.value.thumbnail) formData.append('thumbnail', form.value.thumbnail)
      if (form.value.heroId) formData.append('heroId', form.value.heroId)
      if (form.value.mapId) formData.append('mapId', form.value.mapId)
      if (form.value.gameType) formData.append('gameType', form.value.gameType)

      const response = await fetch('http://localhost:8080/api/videos/upload', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${userStore.token}`
        },
        body: formData
      })

      if (!response.ok) {
        throw new Error('文件上传失败')
      }

      const result = await response.json()
      uploading.value = false

      if (result.id) {
        message.success('上传成功')
        router.push('/videos')
        return
      }
    }

    // 如果没有文件，使用 URL 方式
    await videoApi.upload(form.value)
    message.success('上传成功')
    router.push('/videos')
  }
  catch (e) {
    console.error(e)
    message.error('上传失败')
  }
  finally {
    loading.value = false
    uploading.value = false
  }
}
</script>

<template>
  <div class="video-upload">
    <div class="page-header"><h1 class="page-title">上传视频</h1></div>
    <a-card class="upload-card">
      <a-form :model="form" layout="vertical" @finish="handleSubmit">
        <a-form-item label="标题" required>
          <a-input v-model:value="form.title" placeholder="输入视频标题" />
        </a-form-item>

        <a-form-item label="上传视频文件">
          <a-upload
            :file-list="fileList"
            :before-upload="beforeUpload"
            @change="handleFileChange"
            :multiple="false"
            accept="video/*"
          >
            <a-button>
              <UploadOutlined /> 选择视频文件
            </a-button>
          </a-upload>
          <div class="upload-tip">支持 mp4, webm, mov 格式，最大 500MB</div>
        </a-form-item>

        <a-divider>或</a-divider>

        <a-form-item label="视频链接" :required="!videoFile">
          <a-input v-model:value="form.url" placeholder="输入视频URL" :disabled="!!videoFile" />
        </a-form-item>

        <a-form-item label="封面图片">
          <a-input v-model:value="form.thumbnail" placeholder="输入封面图片URL" />
        </a-form-item>

        <a-form-item label="视频描述">
          <a-textarea v-model:value="form.description" placeholder="输入视频描述" :rows="4" />
        </a-form-item>

        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="使用英雄">
              <a-select v-model:value="form.heroId" placeholder="选择英雄" allowClear>
                <a-select-option v-for="hero in heroes" :key="hero.id" :value="hero.id">{{ hero.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="游戏地图">
              <a-select v-model:value="form.mapId" placeholder="选择地图" allowClear>
                <a-select-option v-for="map in maps" :key="map.id" :value="map.id">{{ map.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="游戏模式">
              <a-select v-model:value="form.gameType">
                <a-select-option v-for="type in gameTypes" :key="type" :value="type">{{ type }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading || uploading">
            {{ uploading ? '上传中...' : '上传视频' }}
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<style scoped>
.video-upload { max-width: 800px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 32px; font-weight: 700; color: var(--text-primary); }
.upload-card { background: var(--bg-card) !important; border-color: var(--border-color) !important; }
.upload-tip { color: var(--text-secondary); font-size: 12px; margin-top: 8px; }
</style>
