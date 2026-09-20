<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelApply, myApplies } from '@/api/apply'
import { formatDateTime } from '@/utils/format'

const router = useRouter()
const applies = ref([])
const loading = ref(false)

async function loadList() {
  loading.value = true
  try {
    const res = await myApplies()
    applies.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(loadList)

/** 取消报名：确认弹窗（契约文案），确认后才调用接口 */
function handleCancel(row) {
  ElMessageBox.confirm('确定要取消该活动的报名吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    await cancelApply(row.activityId)
    ElMessage.success('已取消报名')
    await loadList()
  })
}
</script>

<template>
  <el-card>
    <h3 class="page-title">我的报名</h3>

    <el-table v-loading="loading" :data="applies" style="width: 100%">
      <el-table-column type="index" label="序号" width="70" />
      <el-table-column prop="activityTitle" label="活动标题" min-width="200">
        <template #default="{ row }">
          <el-link type="primary" @click="router.push(`/activity/detail/${row.activityId}`)">
            {{ row.activityTitle }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="报名截止时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.deadline) }}</template>
      </el-table-column>
      <el-table-column label="报名时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button text type="danger" @click="handleCancel(row)">取消报名</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无报名记录，去活动列表看看吧" />
      </template>
    </el-table>
  </el-card>
</template>

<style scoped>
.page-title {
  margin: 0 0 20px;
}
</style>
