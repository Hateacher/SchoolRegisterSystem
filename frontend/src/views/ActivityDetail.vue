<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { activityDetail, deleteActivity } from '@/api/activity'
import { applyActivity, myApplies } from '@/api/apply'
import { getUserId } from '@/utils/auth'
import { formatDateTime, isExpired } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const activity = ref(null)
const applied = ref(false)
const applying = ref(false)

const isOwner = computed(
  () => activity.value && activity.value.createUserId === getUserId(),
)
const isFull = computed(
  () => activity.value && activity.value.appliedCount >= activity.value.maxPeople,
)
const isDeadline = computed(() => activity.value && isExpired(activity.value.deadline))

async function loadDetail() {
  const res = await activityDetail(route.params.id)
  activity.value = res.data
}

async function loadAppliedState() {
  const res = await myApplies()
  const list = res.data || []
  applied.value = list.some((item) => item.activityId === activity.value?.activityId)
}

onMounted(async () => {
  await loadDetail()
  await loadAppliedState().catch(() => {})
})

/** 报名（后端仍会执行四步校验，双保险） */
async function handleApply() {
  applying.value = true
  try {
    const res = await applyActivity(activity.value.activityId)
    ElMessage.success(res.msg || '报名成功')
    await Promise.all([loadDetail(), loadAppliedState()])
  } finally {
    applying.value = false
  }
}

function handleDelete() {
  ElMessageBox.confirm('确定删除（下架）该活动吗？删除后报名记录将一并清除。', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    await deleteActivity(activity.value.activityId)
    ElMessage.success('删除成功')
    router.push('/activity/list')
  })
}
</script>

<template>
  <el-card v-if="activity">
    <div class="header">
      <h3 class="title">{{ activity.title }}</h3>
      <div v-if="isOwner">
        <el-button type="primary" plain @click="router.push(`/activity/edit/${activity.activityId}`)">
          编辑
        </el-button>
        <el-button type="danger" plain @click="handleDelete">删除</el-button>
      </div>
    </div>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="活动标题">{{ activity.title }}</el-descriptions-item>
      <el-descriptions-item label="报名情况">
        <el-tag :type="isFull ? 'danger' : 'success'">
          已报名 {{ activity.appliedCount }} / {{ activity.maxPeople }} 人
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="报名截止时间">
        {{ formatDateTime(activity.deadline) }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDateTime(activity.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="活动描述" :span="2">
        <span class="description">{{ activity.description || '暂无描述' }}</span>
      </el-descriptions-item>
    </el-descriptions>

    <div class="apply-area">
      <!-- 已报名：按钮禁用，文字"已报名" -->
      <el-button v-if="applied" type="success" size="large" disabled>已报名</el-button>
      <!-- 报名截止：按钮置灰，点击提示"活动已截止" -->
      <el-button
        v-else-if="isDeadline"
        type="info"
        size="large"
        :disabled="false"
        @click="ElMessage.warning('活动已截止')"
      >
        立即报名
      </el-button>
      <!-- 名额已满：按钮置灰，点击提示"名额已满" -->
      <el-button
        v-else-if="isFull"
        type="info"
        size="large"
        :disabled="false"
        @click="ElMessage.warning('名额已满')"
      >
        立即报名
      </el-button>
      <!-- 正常：立即报名 -->
      <el-button v-else type="primary" size="large" :loading="applying" @click="handleApply">
        立即报名
      </el-button>
    </div>
  </el-card>
</template>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title {
  margin: 0;
  font-size: 20px;
}

.description {
  white-space: pre-wrap;
  line-height: 1.7;
}

.apply-area {
  margin-top: 24px;
  text-align: center;
}
</style>
