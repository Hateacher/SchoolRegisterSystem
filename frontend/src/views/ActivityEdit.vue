<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { activityDetail, updateActivity } from '@/api/activity'
import { getUserId } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  activityId: null,
  title: '',
  description: '',
  maxPeople: 1,
  deadline: '',
})

const rules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { max: 100, message: '标题不能超过100个字', trigger: 'blur' },
  ],
  maxPeople: [{ required: true, message: '请设置最大报名人数', trigger: 'blur' }],
  deadline: [{ required: true, message: '请选择报名截止时间', trigger: 'change' }],
}

onMounted(async () => {
  const res = await activityDetail(route.params.id)
  const detail = res.data
  // 前端限制 + 后端二次校验：只能编辑本人创建的活动
  if (detail.createUserId !== getUserId()) {
    ElMessage.error('只能编辑自己创建的活动')
    router.push('/activity/list')
    return
  }
  form.activityId = detail.activityId
  form.title = detail.title
  form.description = detail.description
  form.maxPeople = detail.maxPeople
  form.deadline = detail.deadline
})

/** 禁用今天之前的日期（当天仍可选，具体时刻由后端校验必须晚于当前时间） */
function disabledDate(date) {
  return date.getTime() < new Date().setHours(0, 0, 0, 0)
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await updateActivity({
      activityId: form.activityId,
      title: form.title,
      description: form.description,
      maxPeople: form.maxPeople,
      deadline: form.deadline,
    })
    ElMessage.success(res.msg || '修改成功')
    router.push(`/activity/detail/${form.activityId}`)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-card>
    <h3 class="page-title">编辑活动</h3>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="edit-form">
      <el-form-item label="活动标题" prop="title">
        <el-input v-model="form.title" maxlength="100" show-word-limit />
      </el-form-item>
      <el-form-item label="活动描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="5" />
      </el-form-item>
      <el-form-item label="最大报名人数" prop="maxPeople">
        <el-input-number v-model="form.maxPeople" :min="1" :max="10000" />
      </el-form-item>
      <el-form-item label="报名截止时间" prop="deadline">
        <el-date-picker
          v-model="form.deadline"
          type="datetime"
          value-format="YYYY-MM-DDTHH:mm:ss"
          :disabled-date="disabledDate"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">保存修改</el-button>
        <el-button @click="router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<style scoped>
.page-title {
  margin: 0 0 20px;
}

.edit-form {
  max-width: 640px;
}
</style>
