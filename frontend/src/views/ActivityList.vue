<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listActivities } from '@/api/activity'
import { formatDateTime } from '@/utils/format'

const router = useRouter()
const activities = ref([])
const loading = ref(false)

async function loadList() {
  loading.value = true
  try {
    const res = await listActivities()
    activities.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(loadList)
</script>

<template>
  <el-card>
    <div class="toolbar">
      <h3 class="page-title">校园活动</h3>
      <el-button type="primary" @click="router.push('/activity/publish')">发布活动</el-button>
    </div>

    <el-table v-loading="loading" :data="activities" style="width: 100%">
      <el-table-column type="index" label="序号" width="70" />
      <el-table-column prop="title" label="活动标题" min-width="180">
        <template #default="{ row }">
          <el-link type="primary" @click="router.push(`/activity/detail/${row.activityId}`)">
            {{ row.title }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="报名情况" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="row.appliedCount >= row.maxPeople ? 'danger' : 'success'">
            {{ row.appliedCount }}/{{ row.maxPeople }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名截止时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.deadline) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" @click="router.push(`/activity/detail/${row.activityId}`)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无活动，点击右上角发布第一个活动吧" />
      </template>
    </el-table>
  </el-card>
</template>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
}
</style>
